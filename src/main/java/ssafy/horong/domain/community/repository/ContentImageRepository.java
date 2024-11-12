package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.community.entity.ContentByLanguage;
import ssafy.horong.domain.community.entity.ContentImage;

import java.util.List;

public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {

    @Query("SELECT ci.imageUrl FROM ContentImage ci WHERE ci.content = :content")
    List<String> findImageUrlsByContent(@Param("content") ContentByLanguage content);
}
