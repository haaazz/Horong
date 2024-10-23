package ssafy.horong.domain.auth.command;

public record LoginCommand (
        String userId,
        String password
)
{}

