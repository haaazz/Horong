package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 응답 DTO")
public record GetCommentResponse(
        @Schema(description = "댓글 id", example = "1")
        Long id,
        @Schema(description = "댓글 내용", example = "댓글 내용입니다.")
        String content,
        @Schema(description = "댓글 작성자 닉네임", example = "작성자")
        String nickname,
        @Schema(description = "댓글 작성 날짜")
        String createdDate
) {
}
