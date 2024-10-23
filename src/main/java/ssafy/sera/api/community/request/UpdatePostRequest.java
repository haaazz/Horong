package ssafy.sera.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.sera.domain.community.command.UpdatePostCommand;

@Schema(description = "게시글 수정 요청 DTO")
public record UpdatePostRequest(
        @Schema(description = "게시글 id", example = "1")
        Long postId,

        @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
        String title,

        @Schema(description = "게시글 내용", example = "게시글 내용입니다.")
        String content
) {
    public UpdatePostCommand toCommand() {
        return new UpdatePostCommand(postId, title, content);
    }
}
