package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.community.command.CreateCommentCommand;

@Schema(description = "댓글 생성 요청 DTO")
public record CreateCommentRequest(
        @Schema(description = "게시글 id", example = "1")
        Long postId,

        @Schema(description = "댓글 내용", example = "댓글 내용입니다.")
        String content
) {
    public CreateCommentCommand toCommand() {
        return new CreateCommentCommand(postId, content);
    }
}
