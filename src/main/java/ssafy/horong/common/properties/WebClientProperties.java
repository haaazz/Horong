package ssafy.horong.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webclient")
public record WebClientProperties (
        String url
) {
}
