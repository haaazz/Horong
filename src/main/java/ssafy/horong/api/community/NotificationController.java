package ssafy.horong.api.community;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nocifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/{notificationId}")
    public CommonResponse<Void> markAsRead(@PathVariable Long notificationId, @RequestParam Notification.NotificationType type) {
        notificationService.markAsRead(notificationId, type);
        return CommonResponse.ok("알림이 읽음 처리되었습니다.", null);
    }

    @PostMapping("/send")
    public CommonResponse<Void> sendNotification(@RequestParam String message, @RequestParam Long userId) {
        notificationService.sendNotificationToUser(message, userId);
        return CommonResponse.ok("알림이 전송되었습니다.", null);
    }
}
