package ssafy.horong.domain.education.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "education_day")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(name = "education_day_words", joinColumns = @JoinColumn(name = "education_day_id"))
    @Column(name = "word_id")
    private List<Integer> wordIds;

    @Column(name = "day")
    private int day;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
