package ssafy.horong.common.kakao.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "카카오 토큰 만료 응답 DTO")
public record KakaoTokenExpireResponse(
        Long id
) {
    public boolean success() {
        return id != null;
    }
}
