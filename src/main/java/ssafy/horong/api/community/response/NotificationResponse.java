package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.common.exception.Board.PostNotFoundException;
import ssafy.horong.domain.community.entity.ContentByLanguage;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.entity.Post;
import ssafy.horong.domain.community.entity.Message;
import ssafy.horong.domain.member.common.Language;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "알림 응답 DTO") // NotificationResponse 클래스 전체에 대한 설명 추가
public record NotificationResponse(
        @Schema(description = "알림 ID", example = "1")
        Long id,

        @Schema(description = "알림 타입", example = "MESSAGE")
        String type,

        @Schema(description = "알림 메시지", example = "새로운 메시지가 도착했습니다.")
        String message,

        @Schema(description = "게시글 정보", example = "10")
        NotificationPostResponse postContent,

        @Schema(description = "채팅룸 id", example = "1")
        NotificationMessageResponse messageContent,

        @Schema(description = "보낸 사람 ID", example = "1")
        Long senderId,

        @Schema(description = "보낸 사람 닉네임", example = "호롱이")
        String senderName,

        @Schema(description = "알림 생성 시간", example = "2021-07-01T00:00:00")
        LocalDateTime createdAt
) {
    public static List<NotificationResponse> convertToNotificationDTOs(List<Notification> notifications, Language language) {
        return notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getType().name(),
                        notification.getMessageContent(),
                        notification.getPost() != null ? new NotificationPostResponse(
                                notification.getPost().getType().name(),
                                notification.getPost().getId(),
                                getContentByLanguage(notification.getPost(), language)
                        ) : null,
                        notification.getMessage() != null ? new NotificationMessageResponse(
                                notification.getMessage().getId(),
                                getMessageContentByLanguage(notification.getMessage(), language),
                                notification.getType().name(),
                                notification.getMessage().getChatRoom().getId()
                        ) : null,
                        notification.getSender().getId(),
                        notification.getSender().getNickname(),
                        notification.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    private static String getContentByLanguage(Post post, Language language) {
        return post.getContentByCountries().stream()
                .filter(c -> c.getLanguage() == language && c.getContentType() == ContentByLanguage.ContentType.TITLE)
                .findFirst()
                .map(ContentByLanguage::getContent)
                .orElseThrow(PostNotFoundException::new);
    }

    private static String getMessageContentByLanguage(Message message, Language language) {
        return message.getContentByCountries().stream()
                .filter(c -> c.getLanguage() == language)
                .findFirst()
                .map(ContentByLanguage::getContent)
                .orElse("");
    }
}
