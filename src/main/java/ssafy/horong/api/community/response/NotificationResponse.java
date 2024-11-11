package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.community.entity.Notification;
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

        @Schema(description = "콘텐츠 ID", example = "10")
        Long contentId,

        @Schema(description = "보낸 사람 ID", example = "1")
        Long senderId,

        @Schema(description = "보낸 사람 닉네임", example = "호롱이")
        String senderName,

        @Schema(description = "알림 생성 시간", example = "2021-07-01T00:00:00")
        LocalDateTime createdAt
) {
    public static List<NotificationResponse> convertToNotificationDTOs(List<Notification> notifications) {
        return notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getType().name(),
                        notification.getMessage(),
                        notification.getOriginContentId(),
                        notification.getSender().getId(),
                        notification.getSender().getNickname(),
                        notification.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
