package ssafy.horong.api.education.response;

import ssafy.horong.domain.education.entity.Education;

import java.util.List;

public record GetEducationRecordResponse(
        List<EducationRecordResponse> educationRecordList
) {
}
