package ssafy.horong.domain.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.education.entity.EducationDay;

import java.util.Optional;

public interface EducationDayRepository extends JpaRepository<EducationDay, Long> {
    Optional<EducationDay> findByUserId(Long userId);
}
