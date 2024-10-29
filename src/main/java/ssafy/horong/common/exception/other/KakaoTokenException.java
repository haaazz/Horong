package ssafy.horong.common.exception.other;

import lombok.Getter;

@Getter
public class KakaoTokenException extends RuntimeException {
    private int httpStatus;
    private String methodKey;
    private String code;
    private String message;

    public KakaoTokenException(int httpStatus, String methodKey, String code, String message) {
        this.httpStatus = httpStatus;
        this.methodKey = methodKey;
        this.code = code;
        this.message = message;
    }
}
