package ssafy.horong.domain.education.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.member.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "EducationStamp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
