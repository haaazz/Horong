package ssafy.horong.api.education.response;

import java.util.List;

public record GetEducationRecordByWordResponse(
        String word,
        List<EducationRecordResponse> educationRecordList
) {
}
