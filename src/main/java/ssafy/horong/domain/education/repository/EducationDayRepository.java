package ssafy.horong.domain.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.education.entity.EducationDay;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EducationDayRepository extends JpaRepository<EducationDay, Long> {
    Optional<EducationDay> findByUserId(Long userId);

    @Query("SELECT e.day FROM EducationDay e WHERE e.user = :user AND e.createdAt < :today ORDER BY e.day DESC")
    Optional<Integer> findTopByUserAndCreatedAtNotTodayOrderByDayDesc(@Param("user") User user, @Param("today") LocalDateTime today);

}
