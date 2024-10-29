package ssafy.horong.domain.community.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ssafy.horong.api.community.response.GetMessageListResponse;
import ssafy.horong.api.community.response.GetPostResponse;
import ssafy.horong.domain.community.command.*;

import java.util.List;

public interface CommunityService {
    void createPost(CreatePostCommand command);
    void updatePost(UpdatePostCommand command);
    void deletePost(Long id);
    GetPostResponse getPostById(Long id);
    Page<GetPostResponse> getPostList(Pageable pageable);
    Page<GetPostResponse> searchPosts(SearchPostsCommand command, Pageable pageable);
    void createComment(CreateCommentCommand command);
    void updateComment(UpdateCommentCommand command);
    void deleteComment(Long commentId);
    void sendMessage(SendMessageCommand command);
    List<GetMessageListResponse> getMessageList(GetMessageListCommand command);
}
