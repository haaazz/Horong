package ssafy.horong.domain.community.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import ssafy.horong.common.util.NotificationUtil;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.community.service.NotificationService;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationUtil notificationUtil;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void notifyUnreadAlerts() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));

        User user = userRepository.findByUserId(userId.toString()).orElseThrow(null);
        notificationUtil.sendMergedNotifications(user);
    }
}
