package ssafy.horong.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import ssafy.horong.common.properties.ElasticsearchProperties;

import java.util.Arrays;

@Slf4j
@Configuration
@Configurable
@RequiredArgsConstructor
@EnableElasticsearchAuditing
@EnableElasticsearchRepositories
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    private final ElasticsearchProperties elasticsearchProperties;

    @NotNull
    @Override
    public ClientConfiguration clientConfiguration() {
        log.info("Elasticsearch Client Configuration >>>> uris: {}", Arrays.toString(elasticsearchProperties.uris()));
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchProperties.uris())
                .withBasicAuth(elasticsearchProperties.username(), elasticsearchProperties.password())
                .build();
    }

}
