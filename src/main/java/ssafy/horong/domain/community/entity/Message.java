package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<ContentByLanguage> contentByCountries;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private boolean isRead;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isRead = false;
    }

    public void readMessage() {
        this.isRead = true;
    }

    public enum UserMessageType {
        USER, OPPONENT
    }

    // Builder 패턴을 위한 Builder 내부 클래스 정의
    @Builder
    public Message(List<ContentByLanguage> contentByCountries, User user, ChatRoom chatRoom) {
        this.contentByCountries = contentByCountries;
        this.user = user;
        this.chatRoom = chatRoom;
    }
}
