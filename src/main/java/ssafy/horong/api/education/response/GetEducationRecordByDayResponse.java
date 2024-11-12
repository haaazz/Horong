package ssafy.horong.api.education.response;

import java.time.LocalDate;
import java.util.List;

public record GetEducationRecordByDayResponse(
        LocalDate date,
        List<GetEducationRecordByWordResponse> educationRecordList
) {
}
