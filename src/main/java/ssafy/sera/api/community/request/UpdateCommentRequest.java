package ssafy.sera.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.sera.domain.community.command.UpdateCommentCommand;

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
