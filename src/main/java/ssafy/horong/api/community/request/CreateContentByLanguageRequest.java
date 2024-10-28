package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.member.common.Language;

import java.util.List;

public record CreateContentByLanguageRequest(
        @Schema(description = "내용", example = "내용입니다.")
        String content,

        @Schema(description = "원본 여부", example = "true")
        boolean isOriginal,

        @Schema(description = "국가", example = "KOR")
        Language language,

        @Schema(description = "이미지 경로", example = "[{\"imageUrl\": \"http://example.com/image1.jpg\"}, {\"imageUrl\": \"http://example.com/image2.jpg\"}]")
        List<ContentImageRequest> contentImageRequest
) {}
