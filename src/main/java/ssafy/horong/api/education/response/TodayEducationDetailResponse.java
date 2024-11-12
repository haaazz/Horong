package ssafy.horong.api.education.response;

import ssafy.horong.domain.education.entity.Education;

public record TodayEducationDetailResponse(
        Education education,
        boolean isCompleted
) {

}
