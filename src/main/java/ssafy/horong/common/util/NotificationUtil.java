package ssafy.horong.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.repository.NotificationRepository;
import ssafy.horong.domain.member.entity.User;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class NotificationUtil {

    private final NotificationRepository notificationRepository;
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();


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

        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            for (SseEmitter emitter : userEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(combinedMessage));
                } catch (IOException e) {
                    removeEmitter(userId, emitter);
                }
            }
        }
    }

    public SseEmitter createSseEmitter() {
        Long userId = SecurityUtil.getLoginMemberId().orElseThrow(null);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Keep-Alive 기능 유지
        startKeepAlive(emitter, userId);

        return emitter;
    }

    public void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }

    private void startKeepAlive(SseEmitter emitter, Long userId) {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    emitter.send(SseEmitter.event()
                            .name("keepAlive")
                            .data("keep connection alive"));
                } catch (IOException e) {
                    removeEmitter(userId, emitter);
                    timer.cancel();
                }
            }
        }, 0, 5000);
    }

    public Map<Long, List<SseEmitter>> getEmitters() {
        return emitters;
    }
}