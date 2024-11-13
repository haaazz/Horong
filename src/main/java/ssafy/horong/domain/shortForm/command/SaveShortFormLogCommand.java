package ssafy.horong.domain.shortForm.command;

import org.joda.time.DateTime;

public record SaveShortFormLogCommand(
        Long shortFormId,
        DateTime startAt,
        DateTime endAt
) {

}
