package ssafy.horong.common.constant.security;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AUTHENTICATED_PATH {
    public static final String[] AUTHENTICATED_ONLY = {
            "/auth/ping",
            "/auth/logout"
    };
}