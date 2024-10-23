package ssafy.horong.domain.member.command;

import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.member.common.Language;

public record MemberSignupCommand(
        String nickname,
        String password,
        MultipartFile imageUrl,
        String userId,
        Language language
) {
}

