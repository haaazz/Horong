package ssafy.horong.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.elasticsearch")
public record ElasticsearchProperties(
        String username,
        String password,
        String[] uris
) {
}

