package ssafy.horong.api.shortForm.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.shortForm.command.ModifyIsSavedCommand;

@Schema(description = "스크랩 여부 수정 요청 DTO")
public record ModifyIsSavedRequest(
        @Schema(description = "숏폼 ID", example = "1")
        Long shortFormId,
        @Schema(description = "선호도", example = "true")
        Boolean isSaved
) {
        public ModifyIsSavedCommand toCommand() {
                return new ModifyIsSavedCommand(shortFormId, isSaved);
        }
}
