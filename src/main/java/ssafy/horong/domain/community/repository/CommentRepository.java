package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.community.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
