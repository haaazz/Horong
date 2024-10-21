package ssafy.sera.domain.member.command;

public record PasswordUpdateCommand (
        String currentPassword,
        String newPassword,
        String email
){
}
