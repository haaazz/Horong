package ssafy.horong.domain.community.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PostElasticsearchRepository extends ElasticsearchRepository<PostDocument, String> {
    List<PostDocument> findByTitleOrAuthorOrContentKoOrContentZhOrContentJaOrContentEn(
            String title,
            String author,
            String contentKo,
            String contentZh,
            String contentJa,
            String contentEn
    );
}
