package ssafy.horong.domain.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.education.entity.EducationRecord;

import java.util.List;

public interface EducationRecordRepository extends JpaRepository<EducationRecord, Long> {
    List<EducationRecord> findByEducationIdAndUserId(Long educationId, Long userId);
}
