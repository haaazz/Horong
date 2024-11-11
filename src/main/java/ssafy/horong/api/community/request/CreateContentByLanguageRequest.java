package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.member.common.Language;

public record CreateContentByLanguageRequest(
        @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
        String title,

        @Schema(description = "내용", example = "내용입니다.")
        String content,

        @Schema(description = "원본 여부", example = "true")
        boolean isOriginal,

        @Schema(description = "국가", example = "KOR")
        Language language
) {}
