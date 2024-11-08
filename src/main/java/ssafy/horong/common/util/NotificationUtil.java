// NotificationUtil.java
package ssafy.horong.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.repository.NotificationRepository;
import ssafy.horong.domain.member.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class NotificationUtil {

    private final NotificationRepository notificationRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void sendMergedNotifications(User user) {
        List<Notification> unreadCommentNotifications = notificationRepository.findByReceiverAndIsReadFalseAndType(user, Notification.NotificationType.COMMENT);
        List<Notification> unreadMessageNotifications = notificationRepository.findByReceiverAndIsReadFalseAndType(user, Notification.NotificationType.MESSAGE);

        List<String> unreadComments = unreadCommentNotifications.stream()
                .map(Notification::getMessage)
                .collect(Collectors.toList());

        List<String> unreadMessages = unreadMessageNotifications.stream()
                .map(Notification::getMessage)
                .collect(Collectors.toList());

        List<String> combinedNotifications = Stream.concat(unreadComments.stream(), unreadMessages.stream())
                .collect(Collectors.toList());

        sendNotificationToUser(combinedNotifications, user.getId());
    }

    public void sendNotificationToUser(List<String> messages, Long userId) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        String combinedMessage = String.join("\n", messages);

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data("User ID: " + userId + " - " + combinedMessage));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    public SseEmitter gcreateSseEmitter() {
        SseEmitter emitter = new SseEmitter(100000L);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return emitter;
    }

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }
}
