package ssafy.sera.domain.auth.command;

public record TokenRefreshCommand(
        String refreshToken
) {
}
