package ssafy.horong.domain.community.command;

import ssafy.horong.api.community.request.ContentImageRequest;
import ssafy.horong.api.community.request.CreateContentByLanguageRequest;

import java.util.List;

public record SendMessageCommand(
        Long chatRoomId,
        List<CreateContentByLanguageRequest> contentsByLanguages,
        List<ContentImageRequest> contentImageRequest
) {
}
