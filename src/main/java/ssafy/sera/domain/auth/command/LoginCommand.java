package ssafy.sera.domain.auth.command;

public record LoginCommand (
        String authCode,
        String loginType,
        String email,
        String password
)
{}

