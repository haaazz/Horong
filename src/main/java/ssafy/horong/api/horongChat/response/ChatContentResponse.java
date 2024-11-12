package ssafy.horong.api.horongChat.response;

import ssafy.horong.domain.horongChat.entity.HorongChat;

import java.time.LocalDateTime;

public record ChatContentResponse(
        String content,
        HorongChat.AuthorType authorType,
        LocalDateTime createdAt
) {
}
