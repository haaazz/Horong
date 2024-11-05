package ssafy.horong.domain.education.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.member.common.Language;

@Entity
@Table(name = "education_language")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wordId", nullable = false)
    private Education education; // 교육 자료와 연관된 단어

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language; // 언어 (KOREAN, ENGLISH, CHINESE, JAPANESE)

    @Column(length = 20, nullable = false)
    private String transWord; // 번역된 단어

    @Column(length = 100, nullable = false)
    private String transDefinition; // 번역된 정의

    @Column(length = 100, nullable = false)
    private String transExample1; // 번역된 예시 1

    @Column(length = 100, nullable = false)
    private String transExample2; // 번역된 예시 2
}