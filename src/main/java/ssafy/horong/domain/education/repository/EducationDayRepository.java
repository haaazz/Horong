package ssafy.horong.domain.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.education.entity.EducationDay;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EducationDayRepository extends JpaRepository<EducationDay, Long> {
    Optional<EducationDay> findByUserId(Long userId);

    // 특정 사용자의 가장 최근 EducationDay 조회
    @Query("SELECT e FROM EducationDay e WHERE e.user = :user ORDER BY e.createdAt DESC LIMIT 1")
    Optional<EducationDay> findTopByUserOrderByCreatedAtDesc(@Param("user") User user);

    // 특정 사용자의 오늘 날짜를 포함한 가장 최근 day 값 조회
    @Query("SELECT e FROM EducationDay e WHERE e.user = :user AND CAST(e.createdAt AS date) = :today ORDER BY e.day DESC")
    Optional<EducationDay> findTopByUserAndCreatedAtDateOrderByDayDesc(@Param("user") User user,
                                                                       @Param("today") LocalDate today);

    // 특정 사용자의 모든 EducationDay를 생성일자 내림차순으로 조회
    @Query("SELECT e FROM EducationDay e WHERE e.user = :user ORDER BY e.createdAt DESC")
    List<EducationDay> findAllByUserOrderByCreatedAtDesc(@Param("user") User user);
}
