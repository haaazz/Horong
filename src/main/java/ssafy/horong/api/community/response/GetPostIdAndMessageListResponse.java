package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetPostIdAndMessageListResponse(
        @Schema(description = "게시글 id", example = "1")
        Long postId,
        @Schema(description = "메시지 리스트")
        List<GetMessageListResponse> messageList
) {
    public static GetPostIdAndMessageListResponse of(Long postId, List<GetMessageListResponse> messageList) {
        return new GetPostIdAndMessageListResponse(postId, messageList);
    }
}
