package ssafy.horong.common.kakao.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "카카오 토큰 DTO")
public record KakaoToken(
        String tokenType,
        String accessToken,
        String idToken,
        Long expiresIn,
        String refreshToken,
        Long refreshTokenExpiresIn,
        String scope
) {
}
