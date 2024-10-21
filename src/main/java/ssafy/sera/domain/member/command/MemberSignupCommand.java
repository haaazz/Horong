package ssafy.sera.domain.member.command;

import org.springframework.web.multipart.MultipartFile;
import ssafy.sera.domain.member.common.Gender;

import java.time.LocalDate;

public record MemberSignupCommand(
        String nickname,
        String email,
        Gender gender,
        String password,
        LocalDate birth,
        MultipartFile imageUrl,
        String description,
        String number
) {

}

