// NotificationRepository 수정된 부분 포함
package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.member.entity.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverAndIsReadFalse(User receiver); // receiver로 변경
    List<Notification> findByReceiverAndIsReadFalseAndType(User receiver, Notification.NotificationType type); // 특정 타입의 읽지 않은 알림
}
