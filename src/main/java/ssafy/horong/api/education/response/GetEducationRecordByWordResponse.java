package ssafy.horong.api.education.response;

import java.util.List;

public record GetEducationRecordByWordResponse(
        String word,
        Long wordId,
        String pronunciation,
        String definition,
        List<EducationRecordResponse> educationRecordList
) {
}
