package ssafy.horong.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secretKey,
        Long accessTokenExp,
        Long refreshTokenExp
) {
}
