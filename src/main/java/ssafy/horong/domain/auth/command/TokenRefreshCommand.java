package ssafy.horong.domain.auth.command;

public record TokenRefreshCommand(
        String refreshToken
) {
}
