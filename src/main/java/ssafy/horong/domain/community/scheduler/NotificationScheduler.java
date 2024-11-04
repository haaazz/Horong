package ssafy.horong.domain.community.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void notifyUnreadAlerts() {
        List<Notification> unreadCommentNotifications = notificationRepository.findByUserAndIsReadFalseAndType(user, Notification.NotificationType.COMMENT);
        List<Notification> unreadMessageNotifications = notificationRepository.findByUserAndIsReadFalseAndType(user, Notification.NotificationType.MESSAGE);

        // 각각의 알림 메시지를 문자열 리스트로 변환
        List<String> unreadComments = unreadCommentNotifications.stream()
                .map(Notification::getMessage)
                .toList();

        List<String> unreadMessages = unreadMessageNotifications.stream()
                .map(Notification::getMessage)
                .toList();

        // 두 리스트를 병합하여 하나의 리스트로 만듦
        List<String> combinedNotifications = Stream.concat(unreadComments.stream(), unreadMessages.stream())
                .collect(Collectors.toList());

        // 병합된 리스트를 전송
        notificationService.sendNotificationToUser(combinedNotifications, user.getId());
    }
}
