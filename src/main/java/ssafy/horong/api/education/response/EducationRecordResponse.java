package ssafy.horong.api.education.response;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

public record EducationRecordResponse(
        Long id,
        String word,
        float cer,
        List<Integer> gtIdx,
        List<Integer> hypIdx,
        LocalDate date,
        URI audio
) {
}
