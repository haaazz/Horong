package ssafy.horong.domain.education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "education")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String word;

    @Column(length = 100, nullable = false)
    private String definition;

    @Column(length = 100, nullable = false)
    private String example1;

    @Column(length = 100, nullable = false)
    private String example2;

    @Column(length = 255, nullable = false)
    private String audio; // S3 링크

    private LocalDate publishDate;

    @OneToMany(mappedBy = "education", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EducationLanguage> educationLanguages;

    @OneToMany(mappedBy = "education", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EducationRecord> educationRecords;
}