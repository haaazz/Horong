package ssafy.horong.api.education.response;

import ssafy.horong.domain.education.entity.Education;
import ssafy.horong.domain.education.entity.EducationRecord;

import java.util.List;

public record GetEducationRecordResponse(
        Education word,
        List<EducationRecord> translatedWords
) {
}
