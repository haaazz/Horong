package ssafy.horong.api.education.response;

import java.util.List;

public record GetEducationRecordByWordResponse(
        List<EducationRecordResponse> educationRecordList,
        String word
) {
}
