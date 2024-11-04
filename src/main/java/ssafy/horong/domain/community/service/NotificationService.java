package ssafy.horong.domain.community.service;

import ssafy.horong.domain.community.entity.Notification;

public interface NotificationService {
    void markAsRead(Long notificationId, Notification.NotificationType type);
    void sendNotificationToUser(String message, Long userId);
}
