package ssafy.sera.domain.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.sera.api.community.response.getCommentResponse;
import ssafy.sera.api.community.response.getPostResponse;
import ssafy.sera.common.util.S3Util;
import ssafy.sera.common.util.SecurityUtil;
import ssafy.sera.domain.community.command.CreateCommentCommand;
import ssafy.sera.domain.community.command.CreatePostCommand;
import ssafy.sera.domain.community.command.UpdateCommentCommand;
import ssafy.sera.domain.community.command.UpdatePostCommand;
import ssafy.sera.domain.community.entity.BoardType;
import ssafy.sera.domain.community.entity.Post;
import ssafy.sera.domain.community.entity.Comment;
import ssafy.sera.domain.community.repository.BoardRepository;
import ssafy.sera.domain.community.repository.CommentRepository;
import ssafy.sera.domain.member.entity.User;
import ssafy.sera.domain.member.repository.UserRepository;

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

    @Override
    @Transactional
    public void createPost(CreatePostCommand command) {

        Post post = Post.builder()
                .author(getCurrentUser())
                .title(command.title())
                .content(command.content())
                .type(BoardType.valueOf(command.boardType().toUpperCase()))
                .build();
        post.updateImages(s3Util.uploardBoardImageToS3(command.images(), post.getId()));
        boardRepository.save(post);
    }

    @Override
    @Transactional
    public void updatePost(UpdatePostCommand command) {
        Post existingBoard = boardRepository.findById(command.postId())
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        existingBoard.setTitle(command.title());
        existingBoard.setContent(command.title());

        boardRepository.save(existingBoard);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        log.info("게시글 삭제: {}", id);
        boardRepository.deleteById(id);
    }

    @Override
    public getPostResponse getPostById(Long id) {
        log.info("게시글 조회: {}", id);

        // 게시글 조회
        Post post = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시글이 존재하지 않습니다."));

        // 댓글 정보를 DTO로 변환
        List<getCommentResponse> commentResponses = post.getComments().stream()
                .map(comment -> new getCommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthor().getNickname(),
                        comment.getCreatedDate().toString()
                ))
                .toList();

        List<String> presignedUrls = new ArrayList<>();
        for (String image : post.getImages()) {
            presignedUrls.add(s3Util.getPresignedUrlFromS3(image));
        }

        return new getPostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getNickname(),
                presignedUrls,
                commentResponses
        );
    }

    @Override
    public Page<getPostResponse> getPostList(Pageable pageable) {
        log.info("모든 게시글 조회 (페이지네이션)");
        Page<Post> postPage = boardRepository.findAll(pageable);

        List<getPostResponse> postResponses = new ArrayList<>();
        for (Post post : postPage.getContent()) {
            List<getCommentResponse> commentResponses = new ArrayList<>();
            for (Comment comment : post.getComments()) {
                getCommentResponse commentResponse = new getCommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthor().getNickname(),
                        comment.getCreatedDate().toString()
                );
                commentResponses.add(commentResponse);
            }
            List<String> presignedUrls = new ArrayList<>();
            for (String image : post.getImages()) {
                presignedUrls.add(s3Util.getPresignedUrlFromS3(image));
            }
            getPostResponse postResponse = new getPostResponse(
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
        log.info("댓글 삭제: {}", commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void updateComment(UpdateCommentCommand command) {
        Comment existingComment = commentRepository.findById(command.commentId())
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        existingComment.setContent(command.content());
        commentRepository.save(existingComment);
    }

    private User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
    }
}
