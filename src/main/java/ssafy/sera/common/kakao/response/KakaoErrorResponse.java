package ssafy.sera.common.kakao.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "카카오 에러 응답 DTO")
public record KakaoErrorResponse(
        String errorCode,
        String errorDescription
) {
}