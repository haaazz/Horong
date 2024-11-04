package ssafy.horong.domain.community.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import ssafy.horong.domain.community.service.CommunityServiceImpl;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final CommunityServiceImpl communityService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void notifyUnreadAlerts() {
        communityService.sendUnreadNotifications();
    }
}
