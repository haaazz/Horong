package ssafy.sera.api.member.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 회원가입 응답 DTO")
public record UserSignupResponse(
        @Schema(description = "엑세스 토큰", example = "eyJhbGciOisadfawevx12`3121345135")
        String accessToken,

        @Schema(description = "리프레시 토큰", example = "eyJhbGciOisadfawevx12`3121345135")
        String refreshToken
) {
    public static UserSignupResponse of(String accessToken , String refreshToken) {
        return new UserSignupResponse(accessToken, refreshToken);
    }
}
