package ssafy.horong.domain.shortForm.command;

import java.time.LocalDateTime;

public record SaveShortFormLogCommand(
        Long shortFormId,
        LocalDateTime startAt,
        LocalDateTime endAt
) {

}
