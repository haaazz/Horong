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
        // Fetch unread comment notifications
        List<Notification> unreadCommentNotifications = notificationRepository
                .findByReceiverAndIsReadFalseAndType(user, Notification.NotificationType.COMMENT);

        // Fetch unread message notifications
        List<Notification> unreadMessageNotifications = notificationRepository
                .findByReceiverAndIsReadFalseAndType(user, Notification.NotificationType.MESSAGE);

        // Combine both lists into one
        List<Notification> combinedNotifications = new ArrayList<>();
        combinedNotifications.addAll(unreadCommentNotifications);
        combinedNotifications.addAll(unreadMessageNotifications);


        combinedNotifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
        // Send notifications to the user
        sendNotificationToUser(combinedNotifications, user.getId());
    }


    public void sendNotificationToUser(List<Notification> notifications, Long userId) {
        if (notifications == null || notifications.isEmpty()) {
            return;
        }

        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            for (SseEmitter emitter : userEmitters) {
                for (Notification notification : notifications) {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("notification")
                                .data(notification));
                    } catch (IOException e) {
                        removeEmitter(userId, emitter);
                    }
                }
            }
        }
    }

    public SseEmitter createSseEmitter() {
        Long userId = SecurityUtil.getLoginMemberId().orElseThrow();
        SseEmitter emitter = new SseEmitter(600000L); // 10분 타임아웃

        // 해당 userId의 리스트를 초기화하고 emitter 추가
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e) {
            removeEmitter(userId, emitter);
            throw new RuntimeException(e);
        }

        // 일정 시간마다 더미 이벤트 전송
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
                    timer.cancel(); // 연결이 끊어지면 타이머 중단
                }
            }
        }, 0, 10000); // 5초 간격으로 더미 이벤트 전송

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