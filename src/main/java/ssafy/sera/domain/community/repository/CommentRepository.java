package ssafy.sera.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.sera.domain.community.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
