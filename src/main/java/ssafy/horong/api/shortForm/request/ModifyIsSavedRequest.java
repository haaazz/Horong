package ssafy.horong.api.shortForm.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스크랩 여부 수정 요청 DTO")
public record ModifyIsSavedRequest(
        @Schema(description = "숏폼 ID", example = "1")
        Long shortFormId,
        @Schema(description = "선호도", example = "1")
        Boolean isSaved
) {
}
