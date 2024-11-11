package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;

@Schema(description = "댓글 응답 DTO")
public record GetCommentResponse(
        @Schema(description = "댓글 id", example = "1")
        Long id,

        @Schema(description = "댓글 작성자 닉네임", example = "작성자")
        String nickname,

        @Schema(description = "작성자 ID", example = "1")
        Long userId,

        @Schema(description = "댓글의 언어별 콘텐츠", example = "댓글입니다.")
        String contents,

        @Schema(description = "댓글 작성 일시", example = "2021-07-01 00:00:00")
        String createdDate,

        @Schema(description = "작성자 프로필 이미지", example = "https://horong.s3.ap-northeast-2.amazonaws.com/profile/1.jpg")
        URI profileImage
) {
}
