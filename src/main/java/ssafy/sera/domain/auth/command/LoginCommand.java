package ssafy.sera.domain.auth.command;

public record LoginCommand (
        String userId,
        String password
)
{}

