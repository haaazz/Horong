package ssafy.horong.api.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.member.command.UpdateProfileCommand;

@Schema(description = "유저 정보 변경 요청 DTO")
public record UserUpdateRequest(
        @Schema(description = "닉네임", example = "쿠잉비")
        String nickname
) {
    public UpdateProfileCommand toCommand() {
        return new UpdateProfileCommand(nickname);
    }
}
