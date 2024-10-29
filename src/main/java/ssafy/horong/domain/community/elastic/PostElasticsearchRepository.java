package ssafy.horong.domain.community.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ssafy.horong.domain.community.elastic.PostDocument;

public interface PostElasticsearchRepository extends ElasticsearchRepository<PostDocument, Long> {
}