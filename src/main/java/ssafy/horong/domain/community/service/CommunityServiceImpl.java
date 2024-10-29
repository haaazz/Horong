package ssafy.horong.domain.community.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.community.request.CreateContentByLanguageRequest;
import ssafy.horong.api.community.response.GetCommentResponse;
import ssafy.horong.api.community.response.GetMessageListResponse;
import ssafy.horong.api.community.response.GetPostResponse;
import ssafy.horong.common.exception.Board.NotAdminExeption;
import ssafy.horong.common.exception.Board.NotAuthenticatedException;
import ssafy.horong.common.util.S3Util;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.community.command.*;
import ssafy.horong.domain.community.elastic.PostDocument;
import ssafy.horong.domain.community.entity.*;
import ssafy.horong.api.community.request.ContentImageRequest;
import ssafy.horong.domain.community.repository.BoardRepository;
import ssafy.horong.domain.community.repository.CommentRepository;
import ssafy.horong.domain.community.repository.MessageRepository;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.common.MemberRole;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;
import ssafy.horong.domain.community.elastic.PostElasticsearchRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final S3Util s3Util;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final PostElasticsearchRepository postElasticsearchRepository;

    @Transactional
    public void createPost(CreatePostCommand command) {
        validateAdminForNotice(command.boardType());

        // Post 엔티티 빌더 사용
        Post post = Post.builder()
                .title(command.title())
                .type(command.boardType())
                .author(getCurrentUser())
                .build();

        // ContentByLanguage 엔티티 리스트로 변환
        List<ContentByLanguage> contentEntities = command.content().stream()
                .map(c -> {
                    // 각 ContentByCountry의 이미지 매핑
                    List<ContentImage> contentImages = c.contentImageRequest().stream()
                            .map(ContentImageRequest::imageUrl)
                            .map(imageUrl -> {
                                // ContentImage 객체 생성
                                ContentImage contentImage = ContentImage.builder()
                                        .imageUrl(imageUrl)
                                        .build();
                                return contentImage;
                            })
                            .collect(Collectors.toList());

                    // ContentByLanguage 객체 생성
                    ContentByLanguage contentByLanguage = ContentByLanguage.builder()
                            .content(c.content())
                            .isOriginal(c.isOriginal())
                            .language(c.language())
                            .contentType(ContentByLanguage.ContentType.POST)
                            .contentImages(contentImages)
                            .build();

                    // 각 ContentImage에 ContentByLanguage 설정
                    contentImages.forEach(contentImage -> contentImage.setContent(contentByLanguage));

                    // ContentByLanguage에 Post 설정
                    contentByLanguage.setPost(post);

                    return contentByLanguage;
                })
                .collect(Collectors.toList());

        // Post에 ContentByLanguage 설정
        post.setContentByCountries(contentEntities);

        // Post 저장 (ContentByLanguage와 ContentImage도 함께 저장됨)
        boardRepository.save(post);

        // 각 언어별로 개별 PostDocument 생성하여 Elasticsearch에 저장
        for (CreateContentByLanguageRequest contentByLanguage : command.content()) {
            PostDocument postDocument = PostDocument.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .author(post.getAuthor().getNickname())
                    .content(contentByLanguage.content())
                    .language(contentByLanguage.language().name())  // 언어 정보 추가
                    .build();

            postElasticsearchRepository.save(postDocument);  // Elasticsearch에 개별 저장
        }
    }

    @Transactional
    public void updatePost(UpdatePostCommand command) {
        // Post 객체 조회 및 업데이트
        Post post = boardRepository.findById(command.postId())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        post.setTitle(command.title());

        // ContentByLanguage 엔티티 리스트 변환
        List<ContentByLanguage> contentEntities = command.content().stream()
                .map(c -> {
                    List<ContentImage> contentImages = c.contentImageRequest().stream()
                            .map(imageRequest -> {
                                ContentImage contentImage = ContentImage.builder()
                                        .imageUrl(imageRequest.imageUrl())
                                        .build();
                                return contentImage;
                            })
                            .collect(Collectors.toList());

                    ContentByLanguage contentByLanguage = ContentByLanguage.builder()
                            .content(c.content())
                            .isOriginal(c.isOriginal())
                            .language(c.language())
                            .contentType(ContentByLanguage.ContentType.POST)
                            .contentImages(contentImages)
                            .post(post)  // 여기서 Post 연결
                            .build();

                    // ContentImage에 ContentByLanguage 설정
                    contentImages.forEach(contentImage -> contentImage.setContent(contentByLanguage));

                    return contentByLanguage;
                })
                .collect(Collectors.toList());

        // Post에 ContentByLanguage 설정
        post.setContentByCountries(contentEntities);

        // Post 저장 (Cascade로 ContentImage도 저장됨)
        boardRepository.save(post);

        postElasticsearchRepository.deleteById(post.getId());

        // 각 언어별로 개별 PostDocument 생성하여 Elasticsearch에 저장
        for (CreateContentByLanguageRequest contentByLanguage : command.content()) {
            PostDocument postDocument = PostDocument.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .author(post.getAuthor().getNickname())
                    .content(contentByLanguage.content())
                    .language(contentByLanguage.language().name())  // 언어 정보 추가
                    .build();

            postElasticsearchRepository.save(postDocument);  // Elasticsearch에 개별 저장
        }

    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = getPost(id);
        validateUserOrAdmin(post.getAuthor());

        post.setDeletedDate(LocalDateTime.now());
        log.info("게시글 삭제: {}", id);
        postElasticsearchRepository.deleteById(post.getId());
    }

    @Override
    public GetPostResponse getPostById(Long id) {
        log.info("게시글 조회: {}", id);
        Post post = getPost(id);

        // 삭제된 게시글인 경우 예외 처리
        if (post.getDeletedDate() != null) {
            throw new ResourceNotFoundException("삭제된 게시글입니다.");
        }

        Language language = getCurrentUser().getLanguage();

        String content = post.getContentByCountries().stream()
                .filter(c -> c.getLanguage() == language)
                .findFirst()
                .map(ContentByLanguage::getContent)
                .orElseThrow(() -> new ResourceNotFoundException("게시글이 존재하지 않습니다."));

        // 삭제되지 않은 댓글만 필터링
        List<GetCommentResponse> commentResponses = convertToCommentResponse(
                post.getComments().stream()
                        .filter(comment -> comment.getDeletedDate() == null)
                        .collect(Collectors.toList())
        );

        return new GetPostResponse(
                post.getId(),
                post.getTitle(),
                post.getAuthor().getNickname(),
                content,
                commentResponses
        );
    }

    @Override
    public Page<GetPostResponse> getPostList(Pageable pageable) {
        log.info("모든 게시글 조회 (페이지네이션)");

        Page<Post> postPage = boardRepository.findAll(pageable);
        Language language = getCurrentUser().getLanguage();

        List<GetPostResponse> postResponses = postPage.getContent().stream()
                // 삭제된 게시글 필터링
                .filter(post -> post.getDeletedDate() == null)
                .map(post -> {
                    String content = post.getContentByCountries().stream()
                            .filter(c -> c.getLanguage() == language)
                            .findFirst()
                            .map(ContentByLanguage::getContent)
                            .orElseThrow(() -> new ResourceNotFoundException("게시글이 존재하지 않습니다."));

                    // 삭제되지 않은 댓글만 포함
                    List<GetCommentResponse> commentResponses = convertToCommentResponse(
                            post.getComments().stream()
                                    .filter(comment -> comment.getDeletedDate() == null)
                                    .collect(Collectors.toList())
                    );

                    // GetPostResponse 객체 생성
                    return new GetPostResponse(
                            post.getId(),
                            post.getTitle(),
                            post.getAuthor().getNickname(),
                            content,
                            commentResponses
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(postResponses, pageable, postPage.getTotalElements());
    }


    @Override
    @Transactional
    public void createComment(CreateCommentCommand command) {
        Post post = getPost(command.postId());

        // 댓글 생성
        Comment comment = Comment.builder()
                .author(getCurrentUser())
                .board(post)
                .build();

        // 언어별 콘텐츠 생성 및 Comment 설정
        List<ContentByLanguage> contentByCountries = command.contentByCountries().stream()
                .map(contentRequest -> {
                    ContentByLanguage contentByLanguage = ContentByLanguage.builder()
                            .language(contentRequest.language())
                            .content(contentRequest.content())
                            .isOriginal(contentRequest.isOriginal())
                            .contentType(ContentByLanguage.ContentType.COMMENT)
                            .comment(comment) // Comment 설정
                            .build();
                    return contentByLanguage;
                })
                .collect(Collectors.toList());

        // Comment에 언어별 콘텐츠 리스트 설정
        comment.setContentByCountries(contentByCountries);

        // 댓글 저장
        commentRepository.save(comment);
    }


    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        validateUserOrAdmin(comment.getAuthor());

        comment.setDeletedDate(LocalDateTime.now());
        log.info("댓글 삭제: {}", commentId);
    }

    @Override
    @Transactional
    public void updateComment(UpdateCommentCommand command) {
        Comment comment = getComment(command.commentId());
        validateUserOrAdmin(comment.getAuthor()); // 사용자 또는 관리자인지 검증

        // 언어별 콘텐츠가 없으면 예외 처리 또는 기본값 설정
        if (command.contentByCountries() != null && !command.contentByCountries().isEmpty()) {
            // 기존 언어별 콘텐츠 삭제
            comment.getContentByCountries().clear();

            // 새로운 언어별 콘텐츠 추가
            command.contentByCountries().forEach(contentRequest -> {
                ContentByLanguage content = ContentByLanguage.builder()
                        .comment(comment) // 댓글과 연결
                        .language(contentRequest.language()) // 언어 설정
                        .content(contentRequest.content()) // 콘텐츠 설정
                        .isOriginal(contentRequest.isOriginal()) // 원본 여부 설정
                        .contentType(ContentByLanguage.ContentType.COMMENT) // 콘텐츠 타입 설정
                        .build();
                // 언어별 콘텐츠 저장
                comment.getContentByCountries().add(content);
            });
        }

        // updatedDate 설정
        comment.setUpdatedDate(LocalDateTime.now()); // 업데이트 날짜 설정

        // 변경된 댓글 저장
        commentRepository.save(comment);
    }

    private Post getPost(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시글이 존재하지 않습니다."));
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
        return comments.stream()
                .map(comment -> {
                    // 댓글의 언어별 콘텐츠를 가져옴
                    List<ContentByLanguage> contentByCountries = comment.getContentByCountries();
                    String content = contentByCountries.stream()
                            .filter(c -> c.getLanguage() == getCurrentUser().getLanguage()) // 현재 사용자의 언어에 해당하는 콘텐츠를 필터링
                            .findFirst()
                            .map(ContentByLanguage::getContent)
                            .orElse(null); // 언어별 콘텐츠가 없을 경우 기본값 설정

                    return new GetCommentResponse(
                            comment.getId(),
                            comment.getAuthor().getNickname(),
                            content
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public void sendMessage(SendMessageCommand command) {
        String from = getCurrentUser().getNickname();

        // ContentByLanguage 리스트를 생성
        List<ContentByLanguage> contentByCountries = command.contentsByLanguages().stream()
                .map(contentByLanguageCommand -> ContentByLanguage.builder()
                        .language(contentByLanguageCommand.language())
                        .content(contentByLanguageCommand.content())
                        .contentType(ContentByLanguage.ContentType.MESSAGE)
                        .build())
                .collect(Collectors.toList());

        Message message = Message.builder()
                .contentByCountries(contentByCountries)
                .sender(getCurrentUser())
                .receiver(userRepository.findByNickname(command.receiverNickname()))
                .createdAt(LocalDateTime.now())
                .build();

        // Message 객체와 ContentByLanguage 객체 간의 양방향 연관관계 설정
        contentByCountries.forEach(contentByLanguage -> contentByLanguage.setMessage(message));

        // 메시지를 저장
        messageRepository.save(message);
    }


    @Override
    public List<GetMessageListResponse> getMessageList(GetMessageListCommand command) {
        List<Message> messages = messageRepository.findBySenderIdAndreceiver(command.senderId(), getCurrentUser().getId());
        Language userLanguage = getCurrentUser().getLanguage(); // 현재 사용자의 언어 가져오기

        return messages.stream()
                .map(message -> {
                    // 사용자의 언어에 맞는 콘텐츠 가져오기
                    String content = message.getContentByCountries().stream()
                            .filter(c -> c.getLanguage() == userLanguage)
                            .findFirst()
                            .map(ContentByLanguage::getContent)
                            .orElse("기본 메시지 내용"); // 언어에 맞는 콘텐츠가 없을 경우 기본값 설정

                    return new GetMessageListResponse(
                            content,
                            message.getSender().getNickname()
                    );
                })
                .toList();
    }


    private User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
    }

//    @Override
//    public Page<GetPostResponse> searchPosts(SearchPostsCommand command, Pageable pageable) {
//        log.info("게시글 검색: keyword={}, pageable={}", command.keyword(), pageable);
//        Page<Post> postPage = boardRepository.searchByKeyword(command.keyword(), pageable);
//
//        List<GetPostResponse> postResponses = postPage.getContent().stream()
//                .map(this::convertToGetPostResponse)
//                .toList(); // 변경된 부분
//
//        return new PageImpl<>(postResponses, pageable, postPage.getTotalElements());
//    }
//
//    private Long getNextPostId() {
//        Long maxId = boardRepository.findMaxId();
//        return (maxId != null ? maxId : 0L) + 1;
//    }
}
