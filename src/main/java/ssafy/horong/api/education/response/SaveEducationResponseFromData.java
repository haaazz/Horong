package ssafy.horong.api.education.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ssafy.horong.domain.education.entity.Education;

import java.util.List;

public record SaveEducationResponseFromData(
        String text,
        float cer,
        @JsonProperty("gt_idx") List<Integer> gtIdx,
        @JsonProperty("hyp_idx") List<Integer> hypIdx
) {
}
