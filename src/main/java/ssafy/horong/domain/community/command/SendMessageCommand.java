package ssafy.horong.domain.community.command;

import org.springframework.web.multipart.MultipartFile;

public record SendMessageCommand(
        String receiverNickname,
        String content,
        MultipartFile image
) {
}
