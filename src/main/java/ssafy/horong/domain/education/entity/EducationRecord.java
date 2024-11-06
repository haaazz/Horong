package ssafy.horong.domain.education.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.common.util.ListToStringConverter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "education_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "wordId", nullable = false)
    private Education education; // 교육 자료와 연관된 단어

    @Column(nullable = false)
    private float cer; // 인증 점수

    @Column(nullable = false)
    private LocalDate date; // 학습 날짜

    @Column(length = 255, nullable = false)
    private String audio; // S3 링크

    @Convert(converter = ListToStringConverter.class)
    private List<Integer> gtIdx;

    @Convert(converter = ListToStringConverter.class)
    private List<Integer> hypIdx;

    @PrePersist
    public void prePersist() {
        this.date = LocalDate.now();
    }
}
