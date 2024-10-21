package ssafy.sera.api.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.sera.common.exception.User.AbnormalLoginProgressException;
import ssafy.sera.domain.auth.model.UserInfo;

@Schema(description = "인증 응답 DTO")
public record AuthResponse(
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNjM2MzQwMjM2LCJleHAiOjE2MzYzNDAyMzZ9.1J7")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String accessToken,

        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNjMzNjMzNjM2LCJleHAiOjE2MzYzNDAyMzZ9.1J7")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String refreshToken,

        @Schema(description = "회원가입 여부", example = "true")
        Boolean isRegistered,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(description = "카카오 이메일", example = "example@example.com")
        String kakaoEmail
) {
    public static AuthResponse of(String accessToken, String refreshToken) {
        if (accessToken == null || refreshToken == null) {
            throw new AbnormalLoginProgressException();
        }
        return new AuthResponse(accessToken, refreshToken, true, null);
    }

    public static AuthResponse notRegistered(UserInfo userinfo) {
        return new AuthResponse(null, null, false, userinfo.email());
    }
}
