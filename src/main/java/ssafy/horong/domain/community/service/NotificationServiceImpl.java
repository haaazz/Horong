package ssafy.horong.domain.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.repository.NotificationRepository;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final NotificationService notificationService;

    @Transactional
    public void sendNotificationToUser(String message, Long userId) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data("User ID: " + userId + " - " + message));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
        log.info("알림이 전송되었습니다. 사용자 ID: {}, 메시지: {}", userId, message);
    }

    @Transactional
    public void markAsRead(Long notificationId, Notification.NotificationType type) {
        Notification newNotifications = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("알림이 존재하지 않습니다."));
        newNotifications.markAsRead();
        notificationRepository.save(newNotifications);
        User user = getCurrentUser();

        // Use the injected dependency to call the method
        List<Notification> unreadCommentNotifications = notificationRepository.findByUserAndIsReadFalseAndType(user, Notification.NotificationType.COMMENT);
        unreadCommentNotifications.forEach(notification ->
                notificationService.sendNotificationToUser("댓글 알림: " + notification.getMessage(), user.getId())
        );

        List<Notification> unreadMessageNotifications = notificationRepository.findByUserAndIsReadFalseAndType(user, Notification.NotificationType.MESSAGE);
        unreadMessageNotifications.forEach(notification ->
                notificationService.sendNotificationToUser("메시지 알림: " + notification.getMessage(), user.getId())
        );
    }

    private User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
    }
}
