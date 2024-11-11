package ssafy.horong.domain.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.community.entity.BoardType;
import ssafy.horong.domain.community.entity.Post;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Post, Long> {
    Page<Post> findByType(BoardType type, Pageable pageable);
    List<Post> findByTypeOrderByCreatedAtDesc(BoardType boardType, Pageable pageable);
}
