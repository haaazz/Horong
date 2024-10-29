package ssafy.horong.domain.auth.model;

import static ssafy.horong.common.constant.requestheader.AUTHORIZATION.BEARER;

public record BearerToken(
        String accessToken
) {
    public static BearerToken of(String accessToken) {
        return new BearerToken(BEARER + accessToken);
    }
}
