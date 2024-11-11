package ssafy.horong.domain.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.api.community.request.ContentImageRequest;
import ssafy.horong.api.community.request.CreateContentByLanguageRequest;
import ssafy.horong.api.community.response.*;
import ssafy.horong.common.exception.Board.*;
import ssafy.horong.common.util.NotificationUtil;
import ssafy.horong.common.util.S3Util;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.community.command.*;
import ssafy.horong.domain.community.elastic.PostDocument;
import ssafy.horong.domain.community.elastic.PostElasticsearchRepository;
import ssafy.horong.domain.community.entity.*;
import ssafy.horong.domain.community.repository.*;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.common.MemberRole;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ssafy.horong.domain.community.entity.ContentByLanguage.ContentType.CONTENT;
import static ssafy.horong.domain.community.entity.ContentByLanguage.ContentType.TITLE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {

    private final BoardRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final PostElasticsearchRepository postElasticsearchRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationUtil notificationUtil;
    private final S3Util s3Util;
    private final ContentImageRepository contentImageRepository;
    private final ContentByCountryRepository contentByLanguageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void createPost(CreatePostCommand command) {
        validateAdminForNotice(command.boardType());
        validatePostCreateRequest(command.content());

        Post post = Post.builder()
                .type(command.boardType())
                .author(getCurrentUser())
                .build();

        List<ContentImage> contentImages = extractContentImages(command.contentImageRequest());

        List<ContentByLanguage> contentEntities = createContentEntities(command.content(), post, contentImages);

        post.setContentByCountries(contentEntities);
        postRepository.save(post);

        savePostDocument(post, command.content());
        log.info("사용자 {}의 게시글 생성: {}", getCurrentUser().getId(), post.getId());
    }

    @Transactional
    public void updatePost(UpdatePostCommand command) {
        validatePostCreateRequest(command.content());
        Post post = postRepository.findById(command.postId())
                .orElseThrow(PostNotFoundException::new);

        Map<Language, ContentByLanguage> titleContentMap = mapContentByLanguage(post, TITLE);
        Map<Language, ContentByLanguage> mainContentMap = mapContentByLanguage(post, CONTENT);

        for (CreateContentByLanguageRequest c : command.content()) {
            updateOrCreateContent(titleContentMap, post, c, TITLE);
            updateOrCreateContent(mainContentMap, post, c, CONTENT);
        }

        updateContentImages(command, mainContentMap);

        postRepository.save(post);

        savePostDocument(post, command.content());
        log.info("게시글 업데이트: {}", post);
    }

    @Transactional
    @Override
    public void deletePost(Long id) {
        Post post = getPost(id);
        validateUserOrAdmin(post.getAuthor());

        post.setDeletedAt(LocalDateTime.now());
        log.info("게시글 삭제: {}", id);
        postElasticsearchRepository.deleteById(String.valueOf(post.getId()));
    }

    @Transactional
    @Override
    public void createComment(CreateCommentCommand command) {
        Post post = getPost(command.postId());

        Comment comment = Comment.builder()
                .author(getCurrentUser())
                .board(post)
                .build();

        List<ContentByLanguage> contentByCountries = command.contentByCountries().stream()
                .map(contentRequest -> ContentByLanguage.builder()
                        .language(Optional.ofNullable(contentRequest.language()).orElse(null))
                        .content(contentRequest.content())
                        .isOriginal(contentRequest.isOriginal())
                        .comment(comment)
                        .build())
                .toList();

        comment.setContentByCountries(contentByCountries);
        commentRepository.save(comment);

        notifyByPostUser(post.getAuthor(), "게시글에 새로운 댓글이 작성되었습니다: " + command.contentByCountries().get(0).content(), Notification.NotificationType.COMMENT, post.getId());
        log.info("알람 전송: {}", post.getAuthor().getNickname());
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        validateUserOrAdmin(comment.getAuthor());

        comment.setDeletedAt(LocalDateTime.now());
        log.info("댓글 삭제: {}", commentId);
    }

    @Transactional
    @Override
    public void updateComment(UpdateCommentCommand command) {
        Comment comment = getComment(command.commentId());
        validateUserOrAdmin(comment.getAuthor());

        if (command.contentByCountries() != null && !command.contentByCountries().isEmpty()) {
            Map<String, ContentByLanguage> contentMap = comment.getContentByCountries().stream()
                    .collect(Collectors.toMap(
                            content -> content.getLanguage() + "-" + content.isOriginal(),
                            content -> content
                    ));

            for (CreateContentByLanguageRequest contentRequest : command.contentByCountries()) {
                String key = contentRequest.language() + "-" + contentRequest.isOriginal();
                ContentByLanguage existingContent = contentMap.get(key);

                if (existingContent == null) {
                    ContentByLanguage newContent = ContentByLanguage.builder()
                            .comment(comment)
                            .language(contentRequest.language())
                            .isOriginal(contentRequest.isOriginal())
                            .content(contentRequest.content())
                            .build();
                    comment.getContentByCountries().add(newContent);
                } else {
                    existingContent.setContent(contentRequest.content());
                    existingContent.setOriginal(contentRequest.isOriginal());
                }
            }
        }

        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Transactional
    @Override
    public ChatRoom createChatRoom(Long userId, Long postId) {
        ChatRoom chatRoom = ChatRoom.builder()
                .host(getCurrentUser())
                .post(postRepository.findById(postId).orElseThrow(PostNotFoundException::new))
                .guest(userRepository.findById(userId).orElseThrow(null))
                .build();
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    @Transactional
    @Override
    public void sendMessage(SendMessageCommand command) {
        List<ContentImage> contentImages = extractMessageContentImages(command.contentImageRequest());

        List<ContentByLanguage> contentByCountries = command.contentsByLanguages().stream()
                .map(contentByLanguageCommand -> {
                    ContentByLanguage contentEntity = ContentByLanguage.builder()
                            .language(Optional.ofNullable(contentByLanguageCommand.language()).orElse(null))
                            .content(contentByLanguageCommand.content())
                            .contentImages(contentImages)
                            .build();

                    contentImages.forEach(contentImage -> contentImage.setContent(contentEntity));

                    return contentEntity;
                })
                .toList();

        Message message = Message.builder()
                .chatRoom(chatRoomRepository.findById(command.chatRoomId()).orElseThrow(ChatRoomNotFoundException::new))
                .contentByCountries(contentByCountries)
                .user(getCurrentUser())
                .build();

        contentByCountries.forEach(contentByLanguage -> contentByLanguage.setMessage(message));
        messageRepository.save(message);

        User receiver = message.getChatRoom().getOpponent(getCurrentUser());
        notifyByMessageUser(receiver, "메시지가 도착했습니다: " + command.contentsByLanguages().get(0).content(), Notification.NotificationType.MESSAGE, message.getId());
        log.info("메시지 전송: {}", receiver.getNickname());
    }

    @Override
    public List<GetAllMessageListResponse> getAllMessageList() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUser(getCurrentUser());
        log.info("모든 채팅방 조회: {}", chatRooms);

        return chatRooms.stream()
                .map(chatRoom -> {
                    User opponent = chatRoom.getOpponent(getCurrentUser());
                    List<Message> messages = chatRoom.getMessages();

                    long unreadCount = messageRepository.countUnreadMessagesByOpponent(chatRoom, getCurrentUser());

                    messages.sort(Comparator.comparing(Message::getCreatedAt).reversed());

                    Message lastMessage = messages.get(0);
                    String lastContent = getContentByLanguage(lastMessage.getContentByCountries(), getCurrentUser().getLanguage());

                    return new GetAllMessageListResponse(
                            chatRoom.getId(),
                            unreadCount,
                            lastContent,
                            opponent.getNickname(),
                            opponent.getId(),
                            s3Util.getProfilePresignedUrlFromS3(opponent.getProfileImg()),
                            lastMessage.getCreatedAt().toString(),
                            chatRoom.getPost().getId()
                    );
                })
                .sorted(Comparator.comparing(GetAllMessageListResponse::createdAt).reversed())
                .toList();
    }

    @Transactional
    @Override
    public GetPostIdAndMessageListResponse getMessageList(GetMessageListCommand command) {
        List<Message> messages = messageRepository.findAllByChatRoomId(command.roomId());
        Long postId= chatRoomRepository.findPostIdByChatRoomId(command.roomId());
        Language userLanguage = getCurrentUser().getLanguage();
        List<GetMessageListResponse> messageList = messages.stream()
                .map(message -> {
                    String content = getContentByLanguage(message.getContentByCountries(), userLanguage);

                    if (!message.getUser().getId().equals(getCurrentUser().getId())) {
                        message.readMessage();
                        messageRepository.save(message);
                    }

                    Message.UserMessageType userMessageType = message.getUser().getId().equals(getCurrentUser().getId())
                            ? Message.UserMessageType.USER
                            : Message.UserMessageType.OPPONENT;

                    return new GetMessageListResponse(content, message.getUser().getNickname(), message.getUser().getId(), s3Util.getProfilePresignedUrlFromS3(message.getUser().getProfileImg()), message.getCreatedAt().toString(), userMessageType);
                })
                .toList();

        return GetPostIdAndMessageListResponse.of(postId, messageList);
    }

    @Override
    public GetPostResponse getPostById(Long id) {
        log.info("게시글 조회: {}", id);
        Post post = getPost(id);

        if (post.getDeletedAt() != null) {
            throw new PostDeletedException();
        }

        Language language = getCurrentUser().getLanguage();
        String content = getContentByLanguage(post, language, CONTENT);
        String title = getContentByLanguage(post, language, TITLE);

        List<GetCommentResponse> commentResponses = convertToCommentResponse(
                post.getComments().stream().sorted(Comparator.comparing(Comment::getCreatedAt).reversed()).toList()
        );

        return new GetPostResponse(
                post.getId(),
                title,
                post.getAuthor().getNickname(),
                post.getAuthor().getId(),
                content,
                post.getCreatedAt().toString(),
                commentResponses,
                s3Util.getProfilePresignedUrlFromS3(post.getAuthor().getProfileImg())
        );
    }

    @Override
    public Page<GetPostResponse> getPostList(Pageable pageable, String boardType) {
        log.info("모든 게시글 조회 (페이지네이션)");

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Post> postPage = postRepository.findByType(BoardType.valueOf(boardType), sortedPageable);
        Language language = getCurrentUser().getLanguage();

        List<GetPostResponse> postResponses = postPage.getContent().stream()
                .filter(post -> post.getDeletedAt() == null)
                .map(post -> {
                    String content = getContentByLanguage(post, language, CONTENT);
                    String title = getContentByLanguage(post, language, TITLE);

                    List<GetCommentResponse> commentResponses = convertToCommentResponse(
                            post.getComments().stream().sorted(Comparator.comparing(Comment::getCreatedAt).reversed()).toList()
                    );

                    return new GetPostResponse(
                            post.getId(),
                            title,
                            post.getAuthor().getNickname(),
                            post.getAuthor().getId(),
                            content,
                            post.getCreatedAt().toString(),
                            commentResponses,
                            s3Util.getProfilePresignedUrlFromS3(post.getAuthor().getProfileImg())
                    );
                })
                .toList();

        return new PageImpl<>(postResponses, sortedPageable, postPage.getTotalElements());
    }

    @Override
    public Page<GetPostResponse> searchPosts(SearchPostsCommand command, Pageable pageable) {
        String keyword = command.keyword();
        log.info("Elasticsearch 검색 시작: keyword={}", keyword);

        String[] terms = keyword.split("\\s+");
        String userLanguage = getCurrentUser().getLanguage().name();

        Set<PostDocument> uniqueDocuments = Arrays.stream(terms)
                .flatMap(term -> postElasticsearchRepository.findByTitleKoOrTitleZhOrTitleJaOrTitleEnOrAuthorOrContentKoOrContentZhOrContentJaOrContentEn(
                        term, term, term, term, term, term, term, term, term
                ).stream())
                .collect(Collectors.toSet());

        log.info("Elasticsearch 검색 결과: {}", uniqueDocuments);

        List<GetPostResponse> postResponses = uniqueDocuments.stream()
                .map(postDocument -> {
                    String content = getContentByUserLanguage(postDocument, userLanguage, false);
                    String title = getContentByUserLanguage(postDocument, userLanguage, true);

                    Post post = postRepository.findById(postDocument.getPostId()).orElse(null);
                    String createdAt = post != null ? post.getCreatedAt().toString() : "";

                    User author = userRepository.findByNicknameAndIsDeletedFalse(postDocument.getAuthor()).orElse(null);

                    return new GetPostResponse(
                            postDocument.getPostId(),
                            title,
                            postDocument.getAuthor(),
                            author != null ? author.getId() : null,
                            content,
                            createdAt,
                            List.of(),
                            s3Util.getProfilePresignedUrlFromS3(post.getAuthor().getProfileImg())
                    );
                })
                .sorted(Comparator.comparing(GetPostResponse::createdAt).reversed())
                .toList();

        return new PageImpl<>(postResponses, pageable, uniqueDocuments.size());
    }

    public Map<BoardType, List<GetPostResponse>> getMainPostList() {
        log.info("게시판별 게시글 리스트 조회");

        Map<BoardType, List<GetPostResponse>> mainPostList = new HashMap<>();

        mainPostList.put(BoardType.NOTICE, getPostsByBoardType(BoardType.NOTICE, 3));
        mainPostList.put(BoardType.FREE, getPostsByBoardType(BoardType.FREE, 6));
        mainPostList.put(BoardType.SEOUL, getPostsByBoardType(BoardType.SEOUL, 1));
        mainPostList.put(BoardType.BUSAN, getPostsByBoardType(BoardType.BUSAN, 1));
        mainPostList.put(BoardType.INCHEON, getPostsByBoardType(BoardType.INCHEON, 1));
        mainPostList.put(BoardType.GYEONGGI, getPostsByBoardType(BoardType.GYEONGGI, 1));

        return mainPostList;
    }

    public GetOriginPostResponse getOriginalPost(Long id) {
        log.info("원본 게시글 조회: {}", id);
        Post post = getPost(id);

        if (post.getDeletedAt() != null) {
            throw new PostDeletedException();
        }

        ContentByLanguage originalContent = post.getContentByCountries().stream()
                .filter(c -> c.isOriginal() && c.getContentType() == CONTENT)
                .findFirst()
                .orElseThrow(PostNotFoundException::new);

        String content = originalContent.getContent();

        String title = post.getContentByCountries().stream()
                .filter(c -> c.isOriginal() && c.getContentType() == TITLE)
                .findFirst()
                .map(ContentByLanguage::getContent)
                .orElseThrow(PostNotFoundException::new);

        List<GetCommentResponse> commentResponses = convertToCommentResponse(
                post.getComments().stream()
                        .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                        .toList()
        );

        return new GetOriginPostResponse(new GetPostResponse(
                post.getId(),
                title,
                post.getAuthor().getNickname(),
                post.getAuthor().getId(),
                content,
                post.getCreatedAt().toString(),
                commentResponses,
                s3Util.getProfilePresignedUrlFromS3(post.getAuthor().getProfileImg())
        ),
                contentImageRepository.findImageUrlsByContent(originalContent));
    }

    public GetCommentResponse getOriginalComment(Long commentId) {
        Comment comment = getComment(commentId);

        String content = comment.getContentByCountries().stream()
                .filter(c -> c.isOriginal())
                .findFirst()
                .map(ContentByLanguage::getContent)
                .orElseThrow(CommentNotFoundException::new);

        return new GetCommentResponse(
                comment.getId(),
                comment.getAuthor().getNickname(),
                comment.getAuthor().getId(),
                content,
                comment.getCreatedAt().toString(),
                s3Util.getProfilePresignedUrlFromS3(comment.getAuthor().getProfileImg())
        );
    }

    private List<GetPostResponse> getPostsByBoardType(BoardType boardType, int limit) {
        log.info("특정 게시판 타입별 게시글 조회: {}", boardType);

        Language language = getCurrentUser().getLanguage();

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Post> posts = postRepository.findByTypeOrderByCreatedAtDesc(boardType, pageable);
        log.info("{} 게시판에서 가져온 초기 게시글 개수: {}", boardType, posts.size());

        return posts.stream()
                .filter(post -> post.getDeletedAt() == null)
                .map(post -> {
                    String content = getContentByLanguage(post, language, CONTENT);
                    String title = getContentByLanguage(post, language, TITLE);

                    List<GetCommentResponse> commentResponses = convertToCommentResponse(
                            post.getComments().stream().sorted(Comparator.comparing(Comment::getCreatedAt).reversed()).toList()
                    );

                    return new GetPostResponse(
                            post.getId(),
                            title,
                            post.getAuthor().getNickname(),
                            post.getAuthor().getId(),
                            content,
                            post.getCreatedAt().toString(),
                            commentResponses,
                            s3Util.getProfilePresignedUrlFromS3(post.getAuthor().getProfileImg())
                    );
                })
                .sorted(Comparator.comparing(GetPostResponse::createdAt).reversed())
                .toList();
    }

    public String saveImageToS3(MultipartFile file) {
        return s3Util.uploadToS3(file, UUID.randomUUID().toString(), "community/");
    }

    public void validatePostCreateRequest(List<CreateContentByLanguageRequest> contents) {
        for (CreateContentByLanguageRequest request : contents) {
            String safeContent = Jsoup.clean(request.content(), Safelist.none());
            String plainText = escapeHtml(safeContent);

            if (plainText.length() > 255) {
                throw new ContentTooLongExeption();
            }
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
    }

    private void validateUserOrAdmin(User author) {
        if (!author.equals(getCurrentUser()) &&
                SecurityUtil.getLoginMemberRole().orElse(MemberRole.USER) != MemberRole.ADMIN) {
            throw new NotAuthenticatedException();
        }
    }

    private void validateAdminForNotice(BoardType boardType) {
        if (boardType == BoardType.NOTICE &&
                SecurityUtil.getLoginMemberRole().orElse(MemberRole.USER) != MemberRole.ADMIN) {
            throw new NotAdminExeption();
        }
    }

    private List<GetCommentResponse> convertToCommentResponse(List<Comment> comments) {
        log.info("댓글확인 {}", comments);
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(comment -> {
                    if (comment.getDeletedAt() != null) {
                        return new GetCommentResponse(
                                null,
                                "deleted",
                                null,
                                "삭제된 댓글입니다.",
                                null,
                                null
                        );
                    }

                    String content = getContentByLanguage(comment.getContentByCountries(), getCurrentUser().getLanguage());

                    return new GetCommentResponse(
                            comment.getId(),
                            comment.getAuthor().getNickname(),
                            comment.getAuthor().getId(),
                            content,
                            comment.getCreatedAt().toString(),
                            s3Util.getProfilePresignedUrlFromS3(comment.getAuthor().getProfileImg())
                    );
                })
                .toList();
    }

    private void savePostDocument(Post post, List<CreateContentByLanguageRequest> contentByCountries) {
        PostDocument postDocument = postElasticsearchRepository.findById(String.valueOf(post.getId()))
                .orElse(PostDocument.builder()
                        .postId(post.getId())
                        .author(post.getAuthor().getNickname())
                        .authorId(post.getAuthor().getId())
                        .build());

        contentByCountries.forEach(contentByLanguage -> {
            if (contentByLanguage.language() == null) {
                return;
            }
            String language = contentByLanguage.language().name();

            switch (language) {
                case "KOREAN" -> {
                    postDocument.setTitleKo(contentByLanguage.title());
                    postDocument.setContentKo(contentByLanguage.content());
                }
                case "CHINESE" -> {
                    postDocument.setTitleZh(contentByLanguage.title());
                    postDocument.setContentZh(contentByLanguage.content());
                }
                case "JAPANESE" -> {
                    postDocument.setTitleJa(contentByLanguage.title());
                    postDocument.setContentJa(contentByLanguage.content());
                }
                case "ENGLISH" -> {
                    postDocument.setTitleEn(contentByLanguage.title());
                    postDocument.setContentEn(contentByLanguage.content());
                }
            }
        });

        postElasticsearchRepository.save(postDocument);
    }

    private User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
    }

    private List<ContentImage> extractContentImages(List<ContentImageRequest> imageRequests) {
        return imageRequests.stream()
                .map(ContentImageRequest::imageUrl)
                .map(imageUrl -> imageUrl.substring(imageUrl.indexOf("community/")))
                .map(trimmedUrl -> ContentImage.builder().imageUrl(trimmedUrl).build())
                .toList();
    }

    private List<ContentImage> extractMessageContentImages(List<ContentImageRequest> imageRequests) {
        return imageRequests.stream()
                .map(ContentImageRequest::imageUrl)
                .map(imageUrl -> imageUrl.substring(imageUrl.indexOf("community/")))
                .map(trimmedUrl -> ContentImage.builder().imageUrl(trimmedUrl).build())
                .toList();
    }

    private List<ContentByLanguage> createContentEntities(List<CreateContentByLanguageRequest> contents, Post post, List<ContentImage> contentImages) {
        return contents.stream()
                .flatMap(c -> {
                    ContentByLanguage titleEntity = ContentByLanguage.builder()
                            .content(c.title())
                            .isOriginal(c.isOriginal())
                            .language(Optional.ofNullable(c.language()).orElse(null))
                            .contentType(TITLE)
                            .build();
                    titleEntity.setPost(post);

                    List<ContentImage> clonedContentImages = contentImages.stream()
                            .map(img -> ContentImage.builder().imageUrl(img.getImageUrl()).build())
                            .toList();

                    ContentByLanguage contentEntity = ContentByLanguage.builder()
                            .content(c.content())
                            .isOriginal(c.isOriginal())
                            .language(Optional.ofNullable(c.language()).orElse(null))
                            .contentType(CONTENT)
                            .contentImages(clonedContentImages)
                            .build();
                    contentEntity.setPost(post);

                    clonedContentImages.forEach(contentImage -> contentImage.setContent(contentEntity));

                    return Stream.of(titleEntity, contentEntity);
                })
                .toList();
    }

    private Map<Language, ContentByLanguage> mapContentByLanguage(Post post, ContentByLanguage.ContentType contentType) {
        return post.getContentByCountries().stream()
                .filter(content -> content.getContentType() == contentType)
                .collect(Collectors.toMap(ContentByLanguage::getLanguage, content -> content));
    }

    private void updateOrCreateContent(Map<Language, ContentByLanguage> contentMap, Post post, CreateContentByLanguageRequest c, ContentByLanguage.ContentType contentType) {
        ContentByLanguage existingContent = contentMap.get(c.language());
        if (existingContent != null) {
            existingContent.setContent(contentType == TITLE ? c.title() : c.content());
            existingContent.setOriginal(c.isOriginal());
            contentByLanguageRepository.save(existingContent);
        } else {
            ContentByLanguage newContent = ContentByLanguage.builder()
                    .post(post)
                    .language(c.language())
                    .contentType(contentType)
                    .content(contentType == TITLE ? c.title() : c.content())
                    .isOriginal(c.isOriginal())
                    .build();
            post.getContentByCountries().add(newContent);
            contentByLanguageRepository.save(newContent);
        }
    }

    private void updateContentImages(UpdatePostCommand command, Map<Language, ContentByLanguage> mainContentMap) {
        for (CreateContentByLanguageRequest c : command.content()) {
            ContentByLanguage existingMainContent = mainContentMap.get(c.language());
            if (existingMainContent != null) {
                List<ContentImage> existingImages = existingMainContent.getContentImages();
                List<String> newImageUrls = command.contentImageRequest().stream()
                        .map(imageRequest -> imageRequest.imageUrl().substring(imageRequest.imageUrl().indexOf("community/")))
                        .toList();

                existingImages.removeIf(image -> !newImageUrls.contains(image.getImageUrl()));

                for (String imageUrl : newImageUrls) {
                    if (existingImages.stream().noneMatch(image -> image.getImageUrl().equals(imageUrl))) {
                        ContentImage newImage = ContentImage.builder()
                                .imageUrl(imageUrl)
                                .content(existingMainContent)
                                .build();
                        existingImages.add(newImage);
                    }
                }
            }
        }
    }

    private void notifyByPostUser(User receiver, String messageContent, Notification.NotificationType type, Long postId) {
        if (!receiver.equals(getCurrentUser())) {
            // 알림 생성 및 저장
            Notification notification = Notification.builder()
                    .receiver(receiver)
                    .sender(getCurrentUser())
                    .message(messageContent)
                    .originPostId(postId)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .type(type)
                    .build();
            notificationRepository.save(notification);

            // Notification 객체 리스트로 알림 가져오기
            List<Notification> combinedNotifications = getCombinedNotifications(receiver);

            // Notification 객체를 NotificationResponse DTO로 변환
            List<NotificationResponse> notificationDTOs = NotificationResponse.convertToNotificationDTOs(combinedNotifications);

            // 사용자에게 DTO로 알림 전송
            notificationUtil.sendNotificationToUser(notificationDTOs, receiver.getId());

            // 로그 출력
            log.info("알림 목록 전송: {}", notificationDTOs);
        }
    }

    private void notifyByMessageUser(User receiver, String messageContent, Notification.NotificationType type, Long messageId) {
        if (!receiver.equals(getCurrentUser())) {
            // 알림 생성 및 저장
            Notification notification = Notification.builder()
                    .receiver(receiver)
                    .sender(getCurrentUser())
                    .message(messageContent)
                    .originMessageId(messageId)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .type(type)
                    .build();
            notificationRepository.save(notification);

            // Notification 객체 리스트로 알림 가져오기
            List<Notification> combinedNotifications = getCombinedNotifications(receiver);

            // Notification 객체를 NotificationResponse DTO로 변환
            List<NotificationResponse> notificationDTOs = NotificationResponse.convertToNotificationDTOs(combinedNotifications);

            // 사용자에게 DTO로 알림 전송
            notificationUtil.sendNotificationToUser(notificationDTOs, receiver.getId());

            // 로그 출력
            log.info("알림 목록 전송: {}", notificationDTOs);
        }
    }


    private List<Notification> getCombinedNotifications(User receiver) {
        // 사용자의 읽지 않은 모든 알림을 가져옵니다.
        List<Notification> unreadNotifications = notificationRepository.findByReceiverAndIsReadFalse(receiver);
        return unreadNotifications;
    }

    private String getContentByLanguage(Post post, Language language, ContentByLanguage.ContentType contentType) {
        return post.getContentByCountries().stream()
                .filter(c -> c.getLanguage() == language && c.getContentType() == contentType)
                .findFirst()
                .map(ContentByLanguage::getContent)
                .orElseThrow(PostNotFoundException::new);
    }

    private String getContentByLanguage(List<ContentByLanguage> contents, Language language) {
        return contents.stream()
                .filter(c -> c.getLanguage() == language)
                .findFirst()
                .map(ContentByLanguage::getContent)
                .orElse(null);
    }

    private String getContentByUserLanguage(PostDocument postDocument, String userLanguage, boolean isTitle) {
        return switch (userLanguage) {
            case "KOREAN" -> isTitle ? postDocument.getTitleKo() : postDocument.getContentKo();
            case "CHINESE" -> isTitle ? postDocument.getTitleZh() : postDocument.getContentZh();
            case "JAPANESE" -> isTitle ? postDocument.getTitleJa() : postDocument.getContentJa();
            case "ENGLISH" -> isTitle ? postDocument.getTitleEn() : postDocument.getContentEn();
            default -> "";
        };
    }
}
