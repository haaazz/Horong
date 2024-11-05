package ssafy.horong.domain.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.education.entity.EducationRecord;

import java.util.List;
import java.util.Optional;

public interface EducationRecordRepository extends JpaRepository<EducationRecord, Long> {
    List<EducationRecord> findByEducationIdAndUserId(Long educationId, Long userId);
    List<EducationRecord> findByUserId(Long userId);

    @Query("SELECT MAX(er.recordIndex) FROM EducationRecord er WHERE er.education.id = :educationId AND er.userId = :userId")
    Optional<Integer> findMaxRecordIndexByEducationIdAndUserId(@Param("educationId") Long educationId, @Param("userId") Long userId);
}

