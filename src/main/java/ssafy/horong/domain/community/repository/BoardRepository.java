package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.community.entity.Post;

public interface BoardRepository extends JpaRepository<Post, Long> {
}
