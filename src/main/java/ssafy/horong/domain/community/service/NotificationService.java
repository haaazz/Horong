package ssafy.horong.domain.community.service;

import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.community.entity.Notification;


public interface NotificationService {
    void markAsRead(Long notificationId, Notification.NotificationType type);
}
