package ssafy.horong.domain.community.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.api.community.response.GetAllMessageListResponse;
import ssafy.horong.api.community.response.GetCommentResponse;
import ssafy.horong.api.community.response.GetMessageListResponse;
import ssafy.horong.api.community.response.GetPostResponse;
import ssafy.horong.domain.community.command.*;
import ssafy.horong.domain.community.entity.BoardType;
import ssafy.horong.domain.community.entity.Notification;

import java.util.List;
import java.util.Map;

public interface CommunityService {
    void createPost(CreatePostCommand command);
    void updatePost(UpdatePostCommand command);
    void deletePost(Long id);
    GetPostResponse getPostById(Long id);
    Page<GetPostResponse> getPostList(Pageable pageable, String boardType);
    Page<GetPostResponse> searchPosts(SearchPostsCommand command, Pageable pageable);
    void createComment(CreateCommentCommand command);
    void updateComment(UpdateCommentCommand command);
    void deleteComment(Long commentId);
    void sendMessage(SendMessageCommand command);
    List<GetMessageListResponse> getMessageList(GetMessageListCommand command);
    List<GetAllMessageListResponse> getAllMessageList();
    String saveImageToS3(MultipartFile file);
    Map<BoardType, List<GetPostResponse>> getMainPostList();
    GetPostResponse getOriginalPost(Long id);
    GetCommentResponse getOriginalComment(Long commentId);
}
