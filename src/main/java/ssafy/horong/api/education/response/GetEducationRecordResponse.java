package ssafy.horong.api.education.response;

import java.util.List;

public record GetEducationRecordResponse(
        List<EducationRecordResponse> educationRecordList
) {
}
