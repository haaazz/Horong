package ssafy.horong.domain.community.service;

import ssafy.horong.domain.community.entity.Notification;

import java.util.List;

public interface NotificationService {
    void markAsRead(Long notificationId, Notification.NotificationType type);
}
