package ssafy.sera.common.constant.security;

import java.util.List;

public final class CORS_POLICY {
    public static final List<String> ALLOWED_ORIGINS = List.of(
            "*"
    );
    public static final List<String> ALLOWED_METHODS = List.of(
//            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
            "*"
    );

    public static final List<String> ALLOWED_HEADERS = List.of("*");
    public static final List<String> EXPOSED_HEADERS = List.of("Authorization", "Content-Type");
    public static final Boolean ALLOWED_CREDENTIALS = true;

    // PREFLIGHT 요청을 지속시키는 시간
    public static final Long ONE_HOUR = 3600L;

    private CORS_POLICY() {}
}
