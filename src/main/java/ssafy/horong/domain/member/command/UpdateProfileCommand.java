package ssafy.horong.domain.member.command;

import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileCommand(
        String nickname,
        MultipartFile profileImagePath,
        String description,
        boolean deleteImage
) {}
