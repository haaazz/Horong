package ssafy.sera.domain.member.command;

import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileCommand(
        String nickname,
        MultipartFile profileImagePath,
        String description,
        boolean deleteImage
) {}
