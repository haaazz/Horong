package ssafy.sera.domain.community.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ssafy.sera.api.community.response.GetPostResponse;
import ssafy.sera.domain.community.command.CreateCommentCommand;
import ssafy.sera.domain.community.command.CreatePostCommand;
import ssafy.sera.domain.community.command.UpdateCommentCommand;
import ssafy.sera.domain.community.command.UpdatePostCommand;

public interface CommunityService {
    void createPost(CreatePostCommand command);
    void updatePost(UpdatePostCommand command);
    void deletePost(Long id);
    GetPostResponse getPostById(Long id);
    Page<GetPostResponse> getPostList(Pageable pageable);
    void createComment(CreateCommentCommand command);
    void updateComment(UpdateCommentCommand command);
    void deleteComment(Long commentId);
}
