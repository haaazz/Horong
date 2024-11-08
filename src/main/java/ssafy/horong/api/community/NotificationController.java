// NotificationController.java
package ssafy.horong.api.community;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.util.NotificationUtil;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationUtil notificationUtil;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    @PostMapping("/{notificationId}")
    public CommonResponse<Void> markAsRead(@PathVariable Long notificationId, @RequestParam Notification.NotificationType type) {
        notificationService.markAsRead(notificationId, type);
        return CommonResponse.ok("알림이 읽음 처리되었습니다.", null);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "알림 스트림", description = "알림을 스트림으로 전송합니다.")
    @GetMapping("/stream")
    public SseEmitter streamNotifications() {
        return notificationUtil.createSseEmitter();
    }
}
