package ssafy.horong.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.repository.NotificationRepository;
import ssafy.horong.domain.member.entity.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class NotificationUtil {

    private final NotificationRepository notificationRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>(); // SseEmitter 리스트 추가

    public void sendMergedNotifications(User user) {
        // 읽지 않은 댓글과 메시지를 각각 리스트로 가져옴
        List<Notification> unreadCommentNotifications = notificationRepository.findByReceiverAndIsReadFalseAndType(user, Notification.NotificationType.COMMENT);
        List<Notification> unreadMessageNotifications = notificationRepository.findByReceiverAndIsReadFalseAndType(user, Notification.NotificationType.MESSAGE);

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
                emitters.remove(emitter); // 실패한 emitter 즉시 제거
            }
        }
    }

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    public SseEmitter createSseEmitter() {
        SseEmitter emitter = new SseEmitter(100000L); // 100초로 타임아웃 설정

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected")); // 503 에러 방지용 더미 데이터
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return emitter;
    }

}
