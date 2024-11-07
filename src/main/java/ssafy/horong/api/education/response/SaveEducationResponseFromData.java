package ssafy.horong.api.education.response;

import ssafy.horong.domain.education.entity.Education;

import java.util.List;

public record SaveEducationResponseFromData(
        String text,
        float cer,
        List<Integer> gtIdx,
        List<Integer> hypIdx
) {
}
