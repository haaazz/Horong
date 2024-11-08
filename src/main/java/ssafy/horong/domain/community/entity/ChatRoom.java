package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.member.entity.User;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages;

    /**
     * 현재 사용자가 호스트 또는 게스트인지 확인하고,
     * 상대방을 반환하는 메서드.
     *
     */
    public User getOpponent(User currentUser) {
        if (currentUser.equals(host)) {
            return guest;
        } else if (currentUser.equals(guest)) {
            return host;
        } else {
            return host;
        }
    }
}
