package ssafy.horong.api.shortForm.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.joda.time.DateTime;
import ssafy.horong.domain.shortForm.command.SaveShortFormLogCommand;

@Schema(description = "숏폼 로그 저장 요청 DTO")
public record SaveShortFormLogRequest(
        @Schema(description = "숏폼 ID", example = "1")
        Long shortFormId,

        @Schema(description = "시작 시간", example = "2021-08-01T00:00:00")
        DateTime startAt,

        @Schema(description = "종료 시간", example = "2021-08-01T00:00:00")
        DateTime endAt
) {
    public SaveShortFormLogCommand toCommand() {
        return new SaveShortFormLogCommand(shortFormId, startAt, endAt);
    }
}
