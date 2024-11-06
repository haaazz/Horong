package ssafy.horong.domain.community.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            // 인덱스가 존재하는지 확인
            boolean indexExists = elasticsearchClient.indices().exists(e -> e.index("posts")).value();
            if (!indexExists) {
                // 인덱스가 없다면 생성
                CreateIndexRequest request = new CreateIndexRequest.Builder()
                        .index("posts")
                        .settings(s -> s
                                .numberOfShards("1")
                                .numberOfReplicas("1")
                        )
                        .mappings(m -> m
                                .properties("titleKo", p -> p.text(t -> t.analyzer("nori")))
                                .properties("titleZh", p -> p.text(t -> t.analyzer("ik_max_word")))
                                .properties("titleJa", p -> p.text(t -> t.analyzer("kuromoji")))
                                .properties("titleEn", p -> p.text(t -> t.analyzer("english")))
                                .properties("contentKo", p -> p.text(t -> t.analyzer("nori")))
                                .properties("contentZh", p -> p.text(t -> t.analyzer("ik_max_word")))
                                .properties("contentJa", p -> p.text(t -> t.analyzer("kuromoji")))
                                .properties("contentEn", p -> p.text(t -> t.analyzer("english")))
                                .properties("author", p -> p.keyword(k -> k))
                                .properties("postId", p -> p.keyword(k -> k))
                        )
                        .build();

                elasticsearchClient.indices().create(request);
                System.out.println("Elasticsearch 'posts' 인덱스가 생성되었습니다.");
            }
        } catch (Exception e) {
            System.err.println("Elasticsearch 인덱스 생성 오류: " + e.getMessage());
        }
    }
}
