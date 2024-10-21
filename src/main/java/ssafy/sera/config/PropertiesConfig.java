package ssafy.sera.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ssafy.sera.common.properties.*;

@Configuration
@EnableConfigurationProperties({
        CorsProperties.class,
        RedisProperties.class,
        KakaoLoginProperties.class,
        JwtProperties.class,
        S3Properties.class,
        WebClientProperties.class
})
public class PropertiesConfig {
}
