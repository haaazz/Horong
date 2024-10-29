package ssafy.horong.domain.community.command;

import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.api.community.request.CreateContentByLanguageRequest;

import java.util.List;

public record SendMessageCommand(
        String receiverNickname,
        List<CreateContentByLanguageRequest> contentsByLanguages // 다국어 콘텐츠 리스트
) {
}
