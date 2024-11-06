package ssafy.horong.domain.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jsoup.Jsoup;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.api.community.request.CreateContentByLanguageRequest;
import ssafy.horong.api.community.response.GetAllMessageListResponse;
import ssafy.horong.api.community.response.GetCommentResponse;
import ssafy.horong.api.community.response.GetMessageListResponse;
import ssafy.horong.api.community.response.GetPostResponse;
import ssafy.horong.common.exception.Board.*;
import ssafy.horong.common.util.NotificationUtil; // NotificationUtil 추가
import ssafy.horong.common.util.S3Util;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.community.command.*;
import ssafy.horong.domain.community.elastic.PostDocument;
import ssafy.horong.domain.community.entity.*;
import ssafy.horong.api.community.request.ContentImageRequest;
import ssafy.horong.domain.community.repository.BoardRepository;
import ssafy.horong.domain.community.repository.CommentRepository;
import ssafy.horong.domain.community.repository.MessageRepository;
import ssafy.horong.domain.community.repository.NotificationRepository;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.common.MemberRole;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;
import ssafy.horong.domain.community.elastic.PostElasticsearchRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final NotificationUtil notificationUtil; // NotificationUtil 추가
    private final S3Util s3Util;
    private final BoardRepository boardRepository;

    @Transactional
    public void createPost(CreatePostCommand command) {
        validateAdminForNotice(command.boardType());
        validatePostCreateRequest(command.content());

        // Post 엔티티 생성
        Post post = Post.builder()
                .type(command.boardType())
                .author(getCurrentUser())
                .build();
        log.info("이미지 경로 {}", command.contentImageRequest());

        // URL에서 "community/" 이후의 부분만 리스트로 저장
        List<ContentImage> contentImages = command.contentImageRequest().stream()
                .map(ContentImageRequest::imageUrl)
                .map(imageUrl -> imageUrl.substring(imageUrl.indexOf("community/")))
                .map(trimmedUrl -> ContentImage.builder().imageUrl(trimmedUrl).build())
                .toList();

        log.info("이미지 경로 리스트: {}", contentImages.stream()
                .map(ContentImage::getImageUrl)
                .toList()); // 저장된 이미지 URL 리스트 로그 출력

        // ContentByLanguage 리스트 변환 (내용과 제목 모두 처리)
        List<ContentByLanguage> contentEntities = command.content().stream()
                .flatMap(c -> {
                    // 제목 ContentByLanguage 생성
                    ContentByLanguage titleEntity = ContentByLanguage.builder()
                            .content(c.title()) // 제목
                            .isOriginal(c.isOriginal())
                            .language(Optional.ofNullable(c.language()).orElse(null))
                            .contentType(ContentByLanguage.ContentType.TITLE)
                            .build();
                    titleEntity.setPost(post); // Post 설정

                    // 내용 ContentByLanguage 생성
                    ContentByLanguage contentEntity = ContentByLanguage.builder()
                            .content(c.content()) // 내용
                            .isOriginal(c.isOriginal())
                            .language(Optional.ofNullable(c.language()).orElse(null))
                            .contentType(ContentByLanguage.ContentType.CONTENT)
                            .contentImages(contentImages) // 이미지 포함
                            .build();
                    contentEntity.setPost(post); // Post 설정

                    // ContentImage와 ContentByLanguage 관계 설정
                    contentImages.forEach(contentImage -> contentImage.setContent(contentEntity));

                    // 제목과 내용을 모두 포함하는 스트림 반환
                    return Stream.of(titleEntity, contentEntity);
                })
                .toList();

        // Post에 ContentByLanguage 설정
        post.setContentByCountries(contentEntities); // Post에 ContentByLanguage 설정
        postRepository.save(post); // Post 저장

        savePostDocument(post, command.content()); // Elasticsearch에 PostDocument 저장
    }

    @Transactional
    public void updatePost(UpdatePostCommand command) {
        validatePostCreateRequest(command.content());
        Post post = postRepository.findById(command.postId())
                .orElseThrow(PostNotFoundException::new);

        List<ContentByLanguage> updatedContentEntities = command.content().stream()
                .map(c -> {
                    // 기존 ContentByLanguage 엔터티를 찾기
                    ContentByLanguage existingContent = post.getContentByCountries().stream()
                            .filter(content -> content.getLanguage() != null && content.getLanguage().equals(c.language()))
                            .findFirst()
                            .orElseGet(() -> ContentByLanguage.builder()
                                    .post(post)
                                    .language(c.language())
                                    .contentType(ContentByLanguage.ContentType.CONTENT)
                                    .build());

                    // 기존 엔터티의 필드 업데이트
                    existingContent.setContent(c.content());
                    existingContent.setOriginal(c.isOriginal());

                    // 기존 ContentImage 수정 또는 새로 추가
                    List<ContentImage> existingImages = existingContent.getContentImages();
                    List<String> newImageUrls = command.contentImageRequest().stream()
                            .map(ContentImageRequest::imageUrl)
                            .toList();

                    // 기존 이미지 매핑
                    existingImages.forEach(image -> {
                        if (!newImageUrls.contains(image.getImageUrl())) {
                            image.setImageUrl(null); // 혹은 삭제 로직 추가
                        }
                    });

                    // 새로운 이미지 추가
                    newImageUrls.forEach(imageUrl -> {
                        if (existingImages.stream().noneMatch(image -> image.getImageUrl().equals(imageUrl))) {
                            ContentImage newImage = ContentImage.builder()
                                    .imageUrl(imageUrl)
                                    .content(existingContent)
                                    .build();
                            existingImages.add(newImage);
                        }
                    });

                    existingContent.setContentImages(existingImages);

                    return existingContent;
                })
                .toList();

        post.setContentByCountries(updatedContentEntities); // Post에 ContentByLanguage 설정
        postRepository.save(post); // Post 저장
        postElasticsearchRepository.deleteById(String.valueOf(post.getId())); // Elasticsearch에서 기존 PostDocument 삭제
        savePostDocument(post, command.content()); // Elasticsearch에 새로운 PostDocument 저장
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
                        .language(Optional.ofNullable(contentRequest.language()).orElse(null)) // language가 null이면 그대로 null을 사용
                        .content(contentRequest.content())
                        .isOriginal(contentRequest.isOriginal())
                        .comment(comment)
                        .build())
                .toList();

        comment.setContentByCountries(contentByCountries);
        commentRepository.save(comment);

        User postAuthor = post.getAuthor();
        if (!postAuthor.equals(getCurrentUser())) {
            Notification notification = Notification.builder()
                    .user(postAuthor)
                    .message("게시글에 새로운 댓글이 작성되었습니다: " + command.contentByCountries().get(0).content())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
        }

        // 읽지 않은 댓글과 메시지를 각각 리스트로 가져옴
        List<Notification> unreadCommentNotifications = notificationRepository.findByUserAndIsReadFalseAndType(postAuthor, Notification.NotificationType.COMMENT);
        List<Notification> unreadMessageNotifications = notificationRepository.findByUserAndIsReadFalseAndType(postAuthor, Notification.NotificationType.MESSAGE);

        // 각각의 알림 메시지를 문자열 리스트로 변환
        List<String> unreadComments = unreadCommentNotifications.stream()
                .map(Notification::getMessage)
                .toList();

        List<String> unreadMessages = unreadMessageNotifications.stream()
                .map(Notification::getMessage)
                .toList();

        // 두 리스트를 병합하여 하나의 리스트로 만듦
        List<String> combinedNotifications = Stream.concat(unreadComments.stream(), unreadMessages.stream())
                .collect(Collectors.toList());

        // 병합된 리스트를 전송
        notificationUtil.sendNotificationToUser(combinedNotifications, postAuthor.getId()); // 수정된 부분
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
            List<ContentByLanguage> existingContentByCountries = comment.getContentByCountries();

            command.contentByCountries().forEach(contentRequest -> {
                // 기존 ContentByLanguage 엔터티를 찾기
                ContentByLanguage existingContent = existingContentByCountries.stream()
                        .filter(content -> content.getLanguage() != null && content.getLanguage().equals(contentRequest.language()))
                        .findFirst()
                        .orElseGet(() -> {
                            ContentByLanguage newContent = ContentByLanguage.builder()
                                    .comment(comment)
                                    .language(contentRequest.language())
                                    .build();
                            existingContentByCountries.add(newContent);
                            return newContent;
                        });

                // 기존 엔터티의 필드 업데이트
                existingContent.setContent(contentRequest.content());
                existingContent.setOriginal(contentRequest.isOriginal());
            });

            // 삭제할 항목 처리 (기존 리스트에 있지만 새 요청에 없는 항목)
            existingContentByCountries.removeIf(existingContent ->
                    command.contentByCountries().stream()
                            .noneMatch(contentRequest -> contentRequest.language().equals(existingContent.getLanguage()))
            );
        }

        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }


    @Transactional
    @Override
    public void sendMessage(SendMessageCommand command) {
        List<ContentImage> contentImages = command.contentImageRequest().stream()
                .map(ContentImageRequest::imageUrl)
                .map(imageUrl -> {
                    String trimmedUrl = imageUrl.substring(imageUrl.indexOf("community/"));
                    return ContentImage.builder().imageUrl(trimmedUrl).build();
                })
                .toList();

        List<ContentByLanguage> contentByCountries = command.contentsByLanguages().stream()
                .map(contentByLanguageCommand -> {
                    ContentByLanguage contentEntity = ContentByLanguage.builder()
                            .language(Optional.ofNullable(contentByLanguageCommand.language()).orElse(null))
                            .content(contentByLanguageCommand.content())
                            .contentImages(contentImages) // 이미지 포함
                            .build();

                    // ContentImage와 ContentByLanguage 관계 설정
                    contentImages.forEach(contentImage -> contentImage.setContent(contentEntity));

                    return contentEntity;
                })
                .toList();

        User receiver = userRepository.findByNickname(command.receiverNickname())
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

        Message message = Message.builder()
                .contentByCountries(contentByCountries)
                .sender(getCurrentUser())
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .build();

        contentByCountries.forEach(contentByLanguage -> contentByLanguage.setMessage(message));
        messageRepository.save(message);

        // 읽지 않은 댓글과 메시지를 각각 리스트로 가져옴
        List<Notification> unreadCommentNotifications = notificationRepository.findByUserAndIsReadFalseAndType(receiver, Notification.NotificationType.COMMENT);
        List<Notification> unreadMessageNotifications = notificationRepository.findByUserAndIsReadFalseAndType(receiver, Notification.NotificationType.MESSAGE);

        // 각각의 알림 메시지를 문자열 리스트로 변환
        List<String> unreadComments = unreadCommentNotifications.stream()
                .map(Notification::getMessage)
                .toList();

        List<String> unreadMessages = unreadMessageNotifications.stream()
                .map(Notification::getMessage)
                .toList();

        // 두 리스트를 병합하여 하나의 리스트로 만듦
        List<String> combinedNotifications = Stream.concat(unreadComments.stream(), unreadMessages.stream())
                .collect(Collectors.toList());

        // 병합된 리스트를 전송
        notificationUtil.sendNotificationToUser(combinedNotifications, receiver.getId()); // 수정된 부분
    }

    @Override
    public List<GetAllMessageListResponse> getAllMessageList() {
        List<Message> messages = messageRepository.findByReceiverWithContents(getCurrentUser());
        log.info("모든 메시지 조회: {}", messages);

        return messages.stream()
                .collect(Collectors.groupingBy(Message::getSender))
                .entrySet().stream()
                .map(entry -> {
                    User sender = entry.getKey();
                    List<Message> senderMessages = entry.getValue();

                    long unreadCount = senderMessages.stream()
                            .filter(message -> !message.isRead()) // 읽지 않은 메시지만 필터링
                            .count();

                    Message lastMessage = senderMessages.get(senderMessages.size() - 1);
                    String lastContent = lastMessage.getContentByCountries().stream()
                            .filter(c -> c.getLanguage() == getCurrentUser().getLanguage())
                            .findFirst()
                            .map(ContentByLanguage::getContent)
                            .orElse(null);

                    return new GetAllMessageListResponse(
                            unreadCount,
                            lastContent,
                            sender.getNickname()
                    );
                })
                .sorted(Comparator.comparing((GetAllMessageListResponse response) -> {
                    String senderNickname = response.senderNickname();
                    return messages.stream()
                            .filter(m -> m.getSender().getNickname().equals(senderNickname))
                            .max(Comparator.comparing(Message::getCreatedAt))
                            .map(Message::getCreatedAt)
                            .orElse(LocalDateTime.MIN);
                }).reversed())
                .toList();
    }

    @Transactional
    @Override
    public List<GetMessageListResponse> getMessageList(GetMessageListCommand command) {
        List<Message> messages = messageRepository.findBySenderIdAndreceiver(command.senderId(), getCurrentUser().getId());
        Language userLanguage = getCurrentUser().getLanguage();

        return messages.stream()
                .map(message -> {
                    String content = message.getContentByCountries().stream()
                            .filter(c -> c.getLanguage() == userLanguage)
                            .findFirst()
                            .map(ContentByLanguage::getContent)
                            .orElse("기본 메시지 내용");

                    message.readMessage();
                    messageRepository.save(message);

                    log.info("읽음여부 {}, {}" , message.isRead(), message.getId());
                    return new GetMessageListResponse(content, message.getSender().getNickname());
                })
                .toList();
    }

    @Override
    public GetPostResponse getPostById(Long id) {
        log.info("게시글 조회: {}", id);
        Post post = getPost(id);

        if (post.getDeletedAt() != null) {
            throw new PostDeletedException();
        }

        Language language = getCurrentUser().getLanguage();
        String content = post.getContentByCountries().stream()
                .filter(c -> c.getLanguage() == language && c.getContentType() == ContentByLanguage.ContentType.CONTENT)
                .findFirst()
                .map(ContentByLanguage::getContent)
                .orElseThrow(PostNotFoundException::new);

        String title = post.getContentByCountries().stream()
                .filter(c -> c.getLanguage() == language && c.getContentType() == ContentByLanguage.ContentType.TITLE)
                .findFirst()
                .map(ContentByLanguage::getContent)
                .orElseThrow(PostNotFoundException::new);

        List<GetCommentResponse> commentResponses = convertToCommentResponse(
                post.getComments().stream().toList()
        );

        return new GetPostResponse(
                post.getId(),
                title,
                post.getAuthor().getNickname(),
                content,
                post.getCreatedAt().toString(),
                commentResponses
        );
    }

    @Override
    public Page<GetPostResponse> getPostList(Pageable pageable, String boardType) {
        log.info("모든 게시글 조회 (페이지네이션)");

        Page<Post> postPage = postRepository.findByType(BoardType.valueOf(boardType), pageable);
        Language language = getCurrentUser().getLanguage();

        List<GetPostResponse> postResponses = postPage.getContent().stream()
                .filter(post -> post.getDeletedAt() == null)
                .map(post -> {
                    String content = post.getContentByCountries().stream()
                            .filter(c -> c.getLanguage() == language && c.getContentType() == ContentByLanguage.ContentType.CONTENT)
                            .findFirst()
                            .map(ContentByLanguage::getContent)
                            .orElseThrow(PostNotFoundException::new);
                    log.info("post댓글확인 {}", post.getComments());

                    String title = post.getContentByCountries().stream()
                            .filter(c -> c.getLanguage() == language && c.getContentType() == ContentByLanguage.ContentType.TITLE)
                            .findFirst()
                            .map(ContentByLanguage::getContent)
                            .orElseThrow(PostNotFoundException::new);

                    List<GetCommentResponse> commentResponses = convertToCommentResponse(
                            post.getComments().stream().toList()
                    );

                    return new GetPostResponse(
                            post.getId(),
                            title,
                            post.getAuthor().getNickname(),
                            content,
                            post.getCreatedAt().toString(),
                            commentResponses
                    );
                })
                .toList();

        return new PageImpl<>(postResponses, pageable, postPage.getTotalElements());
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

        // 검색 결과를 GetPostResponse로 변환
        List<GetPostResponse> postResponses = uniqueDocuments.stream()
                .map(postDocument -> {
                    // 사용자 언어에 따른 콘텐츠 선택
                    String content = switch (userLanguage) {
                        case "KOREAN" -> postDocument.getContentKo();
                        case "CHINESE" -> postDocument.getContentZh();
                        case "JAPANESE" -> postDocument.getContentJa();
                        case "ENGLISH" -> postDocument.getContentEn();
                        default -> ""; // 언어가 유효하지 않으면 빈 문자열
                    };
                    String title = switch (userLanguage) {
                        case "KOREAN" -> postDocument.getTitleKo();
                        case "CHINESE" -> postDocument.getTitleZh();
                        case "JAPANESE" -> postDocument.getTitleJa();
                        case "ENGLISH" -> postDocument.getTitleEn();
                        default -> ""; // 언어가 유효하지 않으면 빈 문자열
                    };

                    return new GetPostResponse(
                            postDocument.getPostId(),
                            title,
                            postDocument.getAuthor(),
                            content,
                            boardRepository.findById(postDocument.getPostId()).orElse(null).getCreatedAt().toString(),
                            List.of()
                    );
                })
                .toList();

        // 결과를 페이지 형태로 반환
        return new PageImpl<>(postResponses, pageable, uniqueDocuments.size());
    }

    public Map<BoardType, List<GetPostResponse>> getMainPostList() {
        log.info("게시판별 게시글 리스트 조회");

        // 결과를 담을 Map 생성
        Map<BoardType, List<GetPostResponse>> mainPostList = new HashMap<>();

        // 각 BoardType에 대한 게시글을 조회하고 Map에 추가
        mainPostList.put(BoardType.NOTICE, getPostsByBoardType(BoardType.NOTICE, 3));
        mainPostList.put(BoardType.FREE, getPostsByBoardType(BoardType.FREE, 6));
        mainPostList.put(BoardType.SEOUL, getPostsByBoardType(BoardType.SEOUL, 1));
        mainPostList.put(BoardType.BUSAN, getPostsByBoardType(BoardType.BUSAN, 1));
        mainPostList.put(BoardType.INCHEON, getPostsByBoardType(BoardType.INCHEON, 1));
        mainPostList.put(BoardType.GYEONGGI, getPostsByBoardType(BoardType.GYEONGGI, 1));

        return mainPostList;
    }

    private List<GetPostResponse> getPostsByBoardType(BoardType boardType, int limit) {
        log.info("특정 게시판 타입별 게시글 조회: {}", boardType);

        // 현재 사용자의 언어 가져오기
        Language language = getCurrentUser().getLanguage();

        List<Post> posts = postRepository.findByTypeOrderByCreatedAtDesc(boardType, PageRequest.of(0, limit));
        log.info("{} 게시판에서 가져온 초기 게시글 개수: {}", boardType, posts.size());

        return posts.stream()
                .filter(post -> {
                    boolean notDeleted = post.getDeletedAt() == null;
                    log.info("게시글 ID: {}, 삭제 여부: {}", post.getId(), notDeleted);
                    return notDeleted;
                })
                .map(post -> {
                    // 언어별 콘텐츠 필터링
                    String content = post.getContentByCountries().stream()
                            .filter(c -> c.getLanguage() == language && c.getContentType() == ContentByLanguage.ContentType.CONTENT)
                            .findFirst()
                            .map(ContentByLanguage::getContent)
                            .orElseThrow(() -> {
                                log.warn("게시글 ID: {}의 콘텐츠가 지정된 언어로 존재하지 않음", post.getId());
                                return new PostNotFoundException();
                            });

                    String title = post.getContentByCountries().stream()
                            .filter(c -> c.getLanguage() == language && c.getContentType() == ContentByLanguage.ContentType.TITLE)
                            .findFirst()
                            .map(ContentByLanguage::getContent)
                            .orElseThrow(() -> {
                                log.warn("게시글 ID: {}의 제목이 지정된 언어로 존재하지 않음", post.getId());
                                return new PostNotFoundException();
                            });

                    List<GetCommentResponse> commentResponses = convertToCommentResponse(
                            post.getComments().stream().toList()
                    );

                    return new GetPostResponse(
                            post.getId(),
                            title,
                            post.getAuthor().getNickname(),
                            content,
                            post.getCreatedAt().toString(),
                            commentResponses
                    );
                })
                .toList();
    }

    public String saveImageToS3(MultipartFile file) {
        return s3Util.uploadToS3(file, UUID.randomUUID().toString(), "community/");
    }

    public void validatePostCreateRequest(List<CreateContentByLanguageRequest> contents) {
        for (CreateContentByLanguageRequest request : contents) {
            // 모든 HTML 태그와 속성을 제거하여 순수 텍스트만 남김
            String safeContent = Jsoup.clean(request.content(), Safelist.none());

            // HTML 특수문자 수동 이스케이프
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
                .orElseThrow(() -> new ResourceNotFoundException("댓글이 존재하지 않습니다."));
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
                .map(comment -> {
                    // 댓글이 삭제된 경우 처리
                    if (comment.getDeletedAt() != null) {
                        return new GetCommentResponse(
                                null, // 삭제된 댓글의 ID
                                "deleted", // 삭제된 닉네임
                                null,
                                "삭제된 댓글입니다." // 삭제된 댓글 내용
                        );
                    }

                    String content = comment.getContentByCountries().stream()
                            .filter(c -> c.getLanguage() == getCurrentUser().getLanguage())
                            .findFirst()
                            .map(ContentByLanguage::getContent)
                            .orElse(null);

                    return new GetCommentResponse(
                            comment.getId(),
                            comment.getAuthor().getNickname(),
                            content,
                            comment.getCreatedAt().toString()
                    );
                })
                .toList();
    }

    private void savePostDocument(Post post, List<CreateContentByLanguageRequest> contentByCountries) {
        PostDocument postDocument = PostDocument.builder()
                .postId(post.getId())
                .author(post.getAuthor().getNickname())
                .build();

        contentByCountries.forEach(contentByLanguage -> {
            if (contentByLanguage.language() == null) {
                return;
            }
            String language = contentByLanguage.language().name();

            // 언어에 따라 제목 및 콘텐츠 설정
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

        // Elasticsearch에 PostDocument 저장
        postElasticsearchRepository.save(postDocument);
    }

    private User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
    }
}
