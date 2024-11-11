package ssafy.horong.api.community.response;

import ssafy.horong.domain.community.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record NotificationResponse(
        Long id,
        String type,
        String message,
        LocalDateTime createdAt
) {
    // Notification을 NotificationResponse로 변환하는 static 메서드 추가
    public static List<NotificationResponse> convertToNotificationDTOs(List<Notification> notifications) {
        return notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getType().name(),
                        notification.getMessage(),
                        notification.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
