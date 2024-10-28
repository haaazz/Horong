package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.community.entity.ContentImage;

public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {
}
