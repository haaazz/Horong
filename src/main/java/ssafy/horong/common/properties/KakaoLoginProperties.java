package ssafy.horong.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoLoginProperties(
        String contentType,
        String iss,
        String clientId,
        String clientSecret,
        String loginRedirectUri,
        String logoutRedirectUri
) {
}