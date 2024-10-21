package ssafy.sera.domain.auth.command;

import ssafy.sera.common.kakao.model.KakaoToken;
import ssafy.sera.common.properties.KakaoLoginProperties;
import ssafy.sera.common.util.JwtParser;
import ssafy.sera.domain.auth.model.PublicKeys;

public record KakaoLoginCommand(
        String idToken,
        String oauthAccessToken,
        PublicKeys publicKeys,
        String kid,
        String iss,
        String aud
) {
    public static KakaoLoginCommand byKakao(KakaoToken token, PublicKeys publicKeys, JwtParser jwtParser, KakaoLoginProperties properties) {
        return new KakaoLoginCommand(
                token.idToken(),
                token.accessToken(),
                publicKeys,
                jwtParser.getKid(token.idToken()),
                properties.iss(),
                properties.clientId()
        );
    }
}
