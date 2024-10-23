package ssafy.sera.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ssafy.sera.domain.member.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String UserId);

    @Query("""
        SELECT COUNT(p) > 0
        FROM User p
        WHERE p.nickname = :nickname AND p.isDeleted = false
    """)
    boolean existsByNickname(@Param("nickname") String nickname);

    @Query("""
        SELECT p
        FROM User p
        WHERE p.userId = :userId AND p.isDeleted = false
    """)
    Optional<User> findNotDeletedUserByUserId(@Param("userId") String userId);

    @Query("""
        SELECT p
        FROM User p
        WHERE p.isDeleted = true
    """)
    List<User> findDeletedUsers();
}
