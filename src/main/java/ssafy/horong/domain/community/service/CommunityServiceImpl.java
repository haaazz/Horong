package ssafy.horong.domain.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.community.response.GetCommentResponse;
import ssafy.horong.api.community.response.GetMessageListResponse;
import ssafy.horong.api.community.response.GetPostResponse;
import ssafy.horong.common.exception.Board.NotAdminExeption;
import ssafy.horong.common.exception.Board.NotAuthenticatedException;
import ssafy.horong.common.util.S3Util;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.community.command.*;
import ssafy.horong.domain.community.entity.BoardType;
import ssafy.horong.domain.community.entity.Message;
import ssafy.horong.domain.community.entity.Post;
import ssafy.horong.domain.community.entity.Comment;
import ssafy.horong.domain.community.repository.BoardRepository;
import ssafy.horong.domain.community.repository.CommentRepository;
import ssafy.horong.domain.community.repository.MessageRepository;
import ssafy.horong.domain.member.common.MemberRole;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    @Transactional
    public void createPost(CreatePostCommand command) {
        validateAdminForNotice(command.boardType());

        Post post = createPostEntity(command);
        post.updateImages(s3Util.uploardBoardImageToS3(command.images(), getNextPostId()));

        boardRepository.save(post);
    }

    @Override
    @Transactional
    public void updatePost(UpdatePostCommand command) {
        Post post = getPost(command.postId());
        validateUserOrAdmin(post.getAuthor());

        post.setTitle(command.title());
        post.setContent(command.content());
        post.setUpdatedDate(LocalDateTime.now());

        boardRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = getPost(id);
        validateUserOrAdmin(post.getAuthor());

        post.setDeletedDate(LocalDateTime.now());
        log.info("게시글 삭제: {}", id);
    }

    @Override
    public GetPostResponse getPostById(Long id) {
        log.info("게시글 조회: {}", id);
        Post post = getPost(id);

        List<GetCommentResponse> commentResponses = convertToCommentResponse(post.getComments());
        List<String> presignedUrls = post.getImages().stream()
                .map(s3Util::getPresignedUrlFromS3)
                .toList(); // 변경된 부분

        return new GetPostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getNickname(),
                presignedUrls,
                commentResponses
        );
    }

    @Override
    public Page<GetPostResponse> getPostList(Pageable pageable) {
        log.info("모든 게시글 조회 (페이지네이션)");
        Page<Post> postPage = boardRepository.findAll(pageable);

        List<GetPostResponse> postResponses = postPage.getContent().stream()
                .map(this::convertToGetPostResponse)
                .toList(); // 변경된 부분

        return new PageImpl<>(postResponses, pageable, postPage.getTotalElements());
    }

    @Override
    @Transactional
    public void createComment(CreateCommentCommand command) {
        Post post = getPost(command.postId());

        Comment comment = Comment.builder()
                .author(getCurrentUser())
                .content(command.content())
                .board(post)
                .build();
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
        validateUserOrAdmin(comment.getAuthor());

        comment.setContent(command.content());
        comment.setUpdatedDate(LocalDateTime.now());

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

    private Post createPostEntity(CreatePostCommand command) {
        return Post.builder()
                .author(getCurrentUser())
                .title(command.title())
                .content(command.content())
                .type(command.boardType())
                .build();
    }

    private List<GetCommentResponse> convertToCommentResponse(List<Comment> comments) {
        return comments.stream()
                .map(comment -> new GetCommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthor().getNickname()
                ))
                .toList(); // 변경된 부분
    }

    private GetPostResponse convertToGetPostResponse(Post post) {
        List<GetCommentResponse> commentResponses = convertToCommentResponse(post.getComments());
        List<String> presignedUrls = post.getImages().stream()
                .map(s3Util::getPresignedUrlFromS3)
                .toList(); // 변경된 부분

        return new GetPostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getNickname(),
                presignedUrls,
                commentResponses
        );
    }

    @Override
    @Transactional
    public void sendMessage(SendMessageCommand command) {
        String from = getCurrentUser().getNickname();
        String imageUrl = s3Util.uploadImageToS3(command.image(), "from" + from + "to" + command.receiverNickname(), "message/");

        log.info("{}에게 메시지 전송", imageUrl);

        messageRepository.save(Message.builder()
                .content(command.content())
                .image(imageUrl)
                .sender(getCurrentUser())
                .receiver(userRepository.findByNickname(command.receiverNickname()))
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    public List<GetMessageListResponse> getMessageList(GetMessageListCommand command) {
        List<Message> messages = messageRepository.findBySenderIdAndreceiver(command.senderId(), getCurrentUser().getId());

        return messages.stream()
                .map(message -> new GetMessageListResponse(
                        message.getContent(),
                        s3Util.getPresignedUrlFromS3(message.getImage()),
                        message.getSender().getNickname()
                ))
                .toList(); // 변경된 부분
    }

    private User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
    }

    @Override
    public Page<GetPostResponse> searchPosts(SearchPostsCommand command, Pageable pageable) {
        log.info("게시글 검색: keyword={}, pageable={}", command.keyword(), pageable);
        Page<Post> postPage = boardRepository.searchByKeyword(command.keyword(), pageable);

        List<GetPostResponse> postResponses = postPage.getContent().stream()
                .map(this::convertToGetPostResponse)
                .toList(); // 변경된 부분

        return new PageImpl<>(postResponses, pageable, postPage.getTotalElements());
    }

    private Long getNextPostId() {
        Long maxId = boardRepository.findMaxId();
        return (maxId != null ? maxId : 0L) + 1;
    }
}
