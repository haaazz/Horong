package ssafy.horong.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.member.common.PasswordHistory;

import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    List<PasswordHistory> getHistoriesByUserId(Long userId);
}
