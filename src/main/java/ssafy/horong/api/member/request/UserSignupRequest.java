package ssafy.horong.api.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.member.command.MemberSignupCommand;

@Schema(description = "유저 회원가입 요청 DTO")
public record UserSignupRequest(
        @Size(min = 2, max = 20)
        @Schema(description = "닉네임", example = "쿠잉비", minimum = "2", maximum = "20")
        String nickname,

        @Schema(description = "프로필 이미지 파일", example = "profile.jpg")
        MultipartFile image,

        @Schema(description = "비밀번호", example = "password123")
        String password,

        @Schema(description = "유저id", example = "user1")
        String userId
){
        public MemberSignupCommand toCommand() {
                return new MemberSignupCommand(nickname, password, image, userId);
        }
}
