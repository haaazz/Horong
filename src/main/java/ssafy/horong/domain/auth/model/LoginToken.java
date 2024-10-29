package ssafy.horong.domain.auth.model;

public record LoginToken(
        String accessToken,
        String refreshToken
) {
}
