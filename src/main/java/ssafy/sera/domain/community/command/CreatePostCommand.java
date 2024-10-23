package ssafy.sera.domain.community.command;

import org.springframework.web.multipart.MultipartFile;

public record CreatePostCommand(
        String title,
        String content,
        MultipartFile[] images,
        String boardType
) {

}
