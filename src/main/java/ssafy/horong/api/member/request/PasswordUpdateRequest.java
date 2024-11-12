package ssafy.horong.api.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.member.command.PasswordUpdateCommand;

@Schema(description = "비밀번호 변경 요청 DTO")
public record PasswordUpdateRequest(
        @Schema(description = "현재 비밀번호", example = "password123")
        String currentPassword,

        @Schema(description = "변경할 비밀번호", example = "password1234")
        String newPassword
) {
    public PasswordUpdateCommand toCommand() {
        return new PasswordUpdateCommand(currentPassword, newPassword);
    }
}
