package ssafy.horong.api.education.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Schema(description = "날짜와 단어별로 그룹화된 교육 기록의 응답")
public record GetAllEducationRecordResponse(
        @Schema(description = "날짜별로 그룹화된 교육 기록의 응답")
        List<GetEducationRecordByDayResponse> educationRecordList
) {
}
