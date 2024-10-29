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
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Post board;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private LocalDateTime deletedDate;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<ContentByLanguage> contentByCountries;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}
