package ssafy.horong.api.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import ssafy.horong.domain.auth.command.LoginCommand;

@Schema(description = "로그인 요청 DTO")
public record  LoginRequest(
        @Schema(description = "유저 id", example = "test1")
        String userId        ,

        @Schema(description = "비밀번호", example = "password123!")
        String password
) {
        public LoginCommand toCommand() {
                return new LoginCommand(userId, password);
        }
}
