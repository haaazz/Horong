package ssafy.sera.domain.auth.model;

import static ssafy.sera.common.constant.requestheader.AUTHORIZATION.BEARER;

public record BearerToken(
        String accessToken
) {
    public static BearerToken of(String accessToken) {
        return new BearerToken(BEARER + accessToken);
    }
}
