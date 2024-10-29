package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.community.entity.ContentByLanguage;

import java.util.List;

@Schema(description = "댓글 응답 DTO")
public record GetCommentResponse(
        @Schema(description = "댓글 id", example = "1")
        Long id,

        @Schema(description = "댓글 작성자 닉네임", example = "작성자")
        String nickname,

        @Schema(description = "댓글의 언어별 콘텐츠", example = "[{...}, {...}]")
        String contents
) {
}
