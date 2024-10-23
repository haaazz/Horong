package ssafy.horong.common.kakao.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ssafy.horong.common.kakao.response.KakaoTokenExpireResponse;
import ssafy.horong.common.kakao.util.KakaoTokenErrorDecoder;

@FeignClient(
        name = "KakaoLogoutClient",
        url = "https://kapi.kakao.com",
        configuration = KakaoTokenErrorDecoder.class
)
public interface KakaoApiClient {
    @PostMapping("/v1/user/logout")
    KakaoTokenExpireResponse expireAccessToken(
            @RequestHeader("Content-Type") String contentType,
            @RequestHeader("Authorization") String accessToken
    );

}
