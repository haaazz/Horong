package ssafy.horong.domain.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.education.entity.EducationStamp;

import java.time.LocalDate;

public interface EducationStampRepository extends JpaRepository<EducationStamp, Long> {
    boolean existsByUserIdAndDay(Long userId, LocalDate day);
}
