package ssafy.horong.common.constant.redis;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class KEY_PREFIX {
    public static final String ACCESS_TOKEN="access_token:";
    public static final String REFRESH_TOKEN="refresh_token:";
    public static final String BLACKLIST="blacklist:";
}
