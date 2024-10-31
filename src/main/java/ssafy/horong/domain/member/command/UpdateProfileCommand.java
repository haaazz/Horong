package ssafy.horong.domain.member.command;

import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.member.common.Language;

public record UpdateProfileCommand(
        String nickname,
        MultipartFile profileImagePath,
        Language language,
        boolean deleteImage
) {}
