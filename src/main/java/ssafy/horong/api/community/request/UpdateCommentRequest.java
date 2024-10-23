package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.community.command.UpdateCommentCommand;

@Schema(description = "댓글 수정 요청 DTO")
public record UpdateCommentRequest(
        @Schema(description = "댓글 id", example = "1")
        Long commentId,

        @Schema(description = "댓글 내용", example = "댓글 내용입니다.")
        String content
) {
        public UpdateCommentCommand toCommand() {
                return new UpdateCommentCommand(commentId, content);
        }
}
