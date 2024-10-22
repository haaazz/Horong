package ssafy.sera.domain.member.command;

import org.springframework.web.multipart.MultipartFile;

public record MemberSignupCommand(
        String nickname,
        String userId,
        String password,
        MultipartFile imageUrl
) {
}

