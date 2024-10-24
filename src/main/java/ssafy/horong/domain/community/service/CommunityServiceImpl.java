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
import java.util.ArrayList;
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
        if (command.boardType() == BoardType.NOTICE && SecurityUtil.getLoginMemberRole().orElse(MemberRole.USER) != MemberRole.ADMIN) {
            throw new NotAdminExeption();
        }

        Post post = Post.builder()
                .author(getCurrentUser())
                .title(command.title())
                .content(command.content())
                .type(command.boardType())
                .build();
        boardRepository.save(post);
        post.updateImages(s3Util.uploardBoardImageToS3(command.images(), post.getId()));
        boardRepository.save(post);
    }

    @Override
    @Transactional
    public void updatePost(UpdatePostCommand command) {
        Post post = boardRepository.findById(command.postId())
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (post.getAuthor() == getCurrentUser() || SecurityUtil.getLoginMemberRole().orElse(MemberRole.USER) == MemberRole.ADMIN) {
            throw new NotAuthenticatedException();
        }

        post.setTitle(command.title());
        post.setContent(command.content());

        post.setUpdatedDate(LocalDateTime.now());

        boardRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시글이 존재하지 않습니다."));

        if (post.getAuthor() == getCurrentUser() || SecurityUtil.getLoginMemberRole().orElse(MemberRole.USER) == MemberRole.ADMIN) {
            throw new NotAuthenticatedException();
        }

        post.setDeletedDate(LocalDateTime.now());

        log.info("게시글 삭제: {}", id);
        boardRepository.save(post);
    }

    @Override
    public GetPostResponse getPostById(Long id) {
        log.info("게시글 조회: {}", id);

        // 게시글 조회
        Post post = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시글이 존재하지 않습니다."));

        // 댓글 정보를 DTO로 변환
        List<GetCommentResponse> commentResponses = post.getComments().stream()
                .map(comment -> new GetCommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthor().getNickname()
                ))
                .toList();

        List<String> presignedUrls = new ArrayList<>();
        for (String image : post.getImages()) {
            presignedUrls.add(s3Util.getPresignedUrlFromS3(image));
        }

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

        List<GetPostResponse> postResponses = new ArrayList<>();
        for (Post post : postPage.getContent()) {
            List<GetCommentResponse> commentResponses = new ArrayList<>();
            for (Comment comment : post.getComments()) {
                GetCommentResponse commentResponse = new GetCommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthor().getNickname()
                );
                commentResponses.add(commentResponse);
            }
            List<String> presignedUrls = new ArrayList<>();
            for (String image : post.getImages()) {
                presignedUrls.add(s3Util.getPresignedUrlFromS3(image));
            }
            GetPostResponse postResponse = new GetPostResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getAuthor().getNickname(),
                    presignedUrls,
                    commentResponses // 댓글 정보 추가
            );

            postResponses.add(postResponse);
        }

        return new PageImpl<>(postResponses, pageable, postPage.getTotalElements());
    }

    @Override
    @Transactional
    public void createComment(CreateCommentCommand command) {
        Post board = boardRepository.findById(command.postId())
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        Comment commendt = Comment.builder()
                .author(getCurrentUser())
                .content(command.content())
                .board(board)
                .build();
        commentRepository.save(commendt);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글이 존재하지 않습니다."));

        if (comment.getAuthor() == getCurrentUser() || SecurityUtil.getLoginMemberRole().orElse(MemberRole.USER) == MemberRole.ADMIN) {
            throw new NotAuthenticatedException();
        }

        comment.setDeletedDate(LocalDateTime.now());

        log.info("댓글 삭제: {}", commentId);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void updateComment(UpdateCommentCommand command) {
        Comment existingComment = commentRepository.findById(command.commentId())
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        if (existingComment.getAuthor() == getCurrentUser() || SecurityUtil.getLoginMemberRole().orElse(MemberRole.USER) == MemberRole.ADMIN) {
            throw new NotAuthenticatedException();
        }

        existingComment.setUpdatedDate(LocalDateTime.now());

        existingComment.setContent(command.content());
        commentRepository.save(existingComment);
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

        List<GetPostResponse> postResponses = postPage.map(post -> {
            List<GetCommentResponse> commentResponses = post.getComments().stream().map(comment ->
                    new GetCommentResponse(
                            comment.getId(),
                            comment.getContent(),
                            comment.getAuthor().getNickname()
                    )
            ).toList();

            List<String> presignedUrls = post.getImages().stream().map(s3Util::getPresignedUrlFromS3).toList();

            return new GetPostResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getAuthor().getNickname(),
                    presignedUrls,
                    commentResponses
            );
        }).getContent();

        return new PageImpl<>(postResponses, pageable, postPage.getTotalElements());
    }

    @Override
    @Transactional
    public void sendMessage(SendMessageCommand command) {

        String to = command.receiverNickname();
        String from = getCurrentUser().getNickname();
        String text = "from" + from + "to" + to;

        String imageUrl = s3Util.uploadImageToS3(command.image(), text, "message/");
        log.info("{}에게 메시지 전송", imageUrl);
        messageRepository.save(Message.builder()
                .content(command.content())        // 메시지 내용
                .image(imageUrl)                      // 업로드된 이미지 URL
                .sender(getCurrentUser())             // 현재 로그인된 유저를 발신자로 설정
                .receiver(userRepository.findByNickname(to)) // 수신자 설정 (DB에서 조회)
                .createdAt(LocalDateTime.now())       // 생성 시간 설정
                .build());
    }

    @Override
    public List<GetMessageListResponse> getMessageList(GetMessageListCommand command) {
        List<Message> messages = messageRepository.findBySenderIdAndreceiver(command.senderId(), getCurrentUser().getId());
        List<GetMessageListResponse> messageResponses = new ArrayList<>();

        for (Message message : messages) {
            GetMessageListResponse response = new GetMessageListResponse(
                    message.getContent(),
                    s3Util.getPresignedUrlFromS3(message.getImage()),
                    message.getSender().getNickname()
            );
            messageResponses.add(response);
        }
        return messageResponses;
    }

}
