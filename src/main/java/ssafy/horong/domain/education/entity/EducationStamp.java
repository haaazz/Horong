package ssafy.horong.domain.education.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.member.entity.User;
import java.time.LocalDate;

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
    private LocalDate day;

    @Column(nullable = false)
    private LocalDate date;

    @PrePersist
    public void prePersist() {
        this.date = LocalDate.now();
    }
}
