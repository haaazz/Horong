package ssafy.horong.api.shortForm.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.shortForm.command.ModifyPreferenceCommand;

@Schema(description = "선호도 수정 요청 DTO")
public record ModifyPreferenceRequest(
        @Schema(description = "숏폼 ID", example = "1")
        Long shortFormId,
        @Schema(description = "선호도", example = "1")
        Integer preference
) {
    public ModifyPreferenceCommand toCommand() {
        return new ModifyPreferenceCommand(shortFormId, preference);
    }
}
