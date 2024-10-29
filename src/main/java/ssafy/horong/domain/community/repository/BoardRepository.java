package ssafy.horong.domain.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ssafy.horong.domain.community.entity.Post;

public interface BoardRepository extends JpaRepository<Post, Long> {
}
