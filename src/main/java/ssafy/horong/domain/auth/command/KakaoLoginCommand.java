package ssafy.horong.domain.auth.command;

import ssafy.horong.common.kakao.model.KakaoToken;
import ssafy.horong.common.properties.KakaoLoginProperties;
import ssafy.horong.common.util.JwtParser;
import ssafy.horong.domain.auth.model.PublicKeys;

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
