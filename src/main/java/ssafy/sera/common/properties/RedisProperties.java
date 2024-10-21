package ssafy.sera.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(
        String password,
        RedisCluster cluster
) {
    public record RedisCluster(
            Integer maxRedirects,
            List<String> nodes
    ){ }
}
