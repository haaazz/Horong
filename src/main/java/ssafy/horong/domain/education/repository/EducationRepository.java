package ssafy.horong.domain.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.education.entity.Education;

import java.time.LocalDate;
import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByDay(int day);
    Education findByWord(String word);
}