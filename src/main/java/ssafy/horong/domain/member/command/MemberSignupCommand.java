package ssafy.horong.domain.member.command;

import org.springframework.web.multipart.MultipartFile;

public record MemberSignupCommand(
        String nickname,
        String password,
        MultipartFile imageUrl,
        String userId
) {
}

