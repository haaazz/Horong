package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationType type; // 알림 타입 (댓글, 메시지 등)

    public void markAsRead() {
        this.isRead = true;
    }

    public enum NotificationType {
        COMMENT, MESSAGE
    }
}
