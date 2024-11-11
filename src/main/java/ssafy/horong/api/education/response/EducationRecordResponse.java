package ssafy.horong.api.education.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.util.List;

@Schema(description = "개별 교육 기록의 세부 정보")
public record EducationRecordResponse(
        @Schema(description = "교육 기록의 고유 식별자", example = "1")
        Long id,

        @Schema(description = "인증 점수 (백분율)", example = "100")
        float cer,

        @Schema(description = "정답 인덱스 목록", example = "[0, 1, 2, 3, 4]")
        List<Integer> gtIdx,

        @Schema(description = "예측 인덱스 목록", example = "[0, 1, 2, 3, 4]")
        List<Integer> hypIdx,

        @Schema(description = "이 기록과 연관된 오디오 파일의 URI 링크", example = "https://example.com/audio.mp3")
        URI audio
) {
}
