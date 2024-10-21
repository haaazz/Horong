package ssafy.sera.api.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import ssafy.sera.domain.member.command.UpdateProfileCommand;

@Schema(description = "선수 정보 변경 요청 DTO")
public record UserUpdateRequest(
        @Size(min = 2, max = 20)
        @Schema(description = "닉네임", example = "쿠잉비")
        String nickname,

        @Schema(description = "프로필 이미지 파일")
        MultipartFile profileImagePath,

        @Size(max = 50)
        @Schema(description = "한 줄 소개", example = "안녕하세요")
        String description,

        @Schema(description = "사진 삭제 여부", example = "true")
        boolean deleteImage
) {
    public UpdateProfileCommand toCommand() {
        return new UpdateProfileCommand(nickname, profileImagePath, description, deleteImage);
    }
}
