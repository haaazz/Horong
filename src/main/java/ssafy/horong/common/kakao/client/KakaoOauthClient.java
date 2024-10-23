package ssafy.horong.common.kakao.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ssafy.horong.common.kakao.model.KakaoToken;
import ssafy.horong.common.kakao.util.KakaoTokenErrorDecoder;
import ssafy.horong.domain.auth.model.PublicKeys;

@FeignClient(
        name = "KakaoTokenClient",
        url = "https://kauth.kakao.com",
        configuration = KakaoTokenErrorDecoder.class
)
public interface KakaoOauthClient {
    @PostMapping("/oauth/token?grant_type=authorization_code")
    KakaoToken getToken(
            @RequestHeader("Content-Type") String contentType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code,
            @RequestParam("client_secret") String clientSecret
    );

    @GetMapping("/.well-known/jwks.json")
    PublicKeys getPublicKeys();

    @GetMapping("/oauth/logout")
    void logout(
            @RequestParam("client_id") String clientId,
            @RequestParam("logout_redirect_uri") String logoutRedirectUri,
            @RequestParam("state") String state
    );
}
