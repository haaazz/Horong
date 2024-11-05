package ssafy.horong.api.education.response;

import ssafy.horong.domain.education.entity.Education;

import java.net.URI;
import java.time.LocalDate;

public record EducationRecordResponse(
        Long id,
        Education education,
        float cer,
        LocalDate date,
        URI audio
        ) {
}
