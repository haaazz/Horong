package ssafy.horong.domain.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.community.entity.BoardType;
import ssafy.horong.domain.community.entity.Post;

import java.util.List;

public interface BoardRepository extends JpaRepository<Post, Long> {
    Page<Post> findByType(BoardType type, Pageable pageable);
    List<Post> findTopByBoardTypeOrderByCreatedDateDesc(BoardType boardType, Pageable pageable);
}
