package ssafy.horong.api.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.auth.command.TokenRefreshCommand;

@Schema(description = "토큰 Refresh 요청 DTO")
public record TokenRefreshRequest(
        String refreshToken
) {
    public TokenRefreshCommand toCommand(){
        return new TokenRefreshCommand(refreshToken);
    }
}
