package ssafy.horong.domain.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.education.entity.EducationStamp;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDate;

public interface EducationStampRepository extends JpaRepository<EducationStamp, Long> {
    @Query("SELECT CASE WHEN COUNT(es) > 0 THEN true ELSE false END " +
            "FROM EducationStamp es " +
            "WHERE es.user.id = :userId AND DATE(es.createdAt) = :day")
    boolean existsByUserIdAndCreatedAtDateOnly(@Param("userId") Long userId, @Param("day") LocalDate day);

    int countByUser(User user);
}
