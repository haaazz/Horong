package ssafy.horong.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ssafy.horong.common.properties.*;

@Configuration
@EnableConfigurationProperties({
        CorsProperties.class,
        RedisProperties.class,
        KakaoLoginProperties.class,
        JwtProperties.class,
        S3Properties.class,
        WebClientProperties.class,
        ElasticsearchProperties.class,
})
public class PropertiesConfig {
}
