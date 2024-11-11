package ssafy.horong.api.education.response;

import java.time.LocalDate;
import java.util.List;

public record GetEducationRecordByDayResponse(
        List<GetEducationRecordByWordResponse> educationRecordList,
        LocalDate date
) {
}
