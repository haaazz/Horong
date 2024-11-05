package ssafy.horong.domain.community.command;

import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.api.community.request.ContentImageRequest;
import ssafy.horong.api.community.request.CreateContentByLanguageRequest;

import java.util.List;

public record SendMessageCommand(
        String receiverNickname,
        List<CreateContentByLanguageRequest> contentsByLanguages,
        List<ContentImageRequest> contentImageRequest
) {
}
