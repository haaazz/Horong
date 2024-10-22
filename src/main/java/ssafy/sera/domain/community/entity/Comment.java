package ssafy.sera.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.sera.domain.member.entity.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(length = 50, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
}
