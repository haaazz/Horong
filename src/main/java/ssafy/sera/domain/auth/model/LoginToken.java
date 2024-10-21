package ssafy.sera.domain.auth.model;

public record LoginToken(
        String accessToken,
        String refreshToken
) {
}
