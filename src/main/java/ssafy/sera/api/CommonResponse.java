package ssafy.sera.api;


import com.fasterxml.jackson.annotation.JsonInclude;
import ssafy.sera.common.exception.errorcode.BaseErrorCode;

import static ssafy.sera.api.StatusCode.*;

public record CommonResponse<T>(
        String code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T result
) {
    public static <T> CommonResponse<T> ok(String message, T data) {
        return new CommonResponse<>(OK.toString(), message, data);
    }
    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(OK.toString(), "호출에 성공했습니다.", data);
    }
    public static <T> CommonResponse<T> ok() {
        return new CommonResponse<>(OK.toString(), "호출에 성공했습니다.", null);
    }
    public static <T> CommonResponse<T> created(String message) {
        return new CommonResponse<>(CREATED.toString(), message, null);
    }
    public static <T> CommonResponse<T> created(String message, T data) {
        return new CommonResponse<>(CREATED.toString(), message, data);
    }
    public static <T> CommonResponse<T> noContent() {
        return new CommonResponse<>(NO_CONTENT.toString(), "정상적으로 삭제되었습니다.", null);
    }
    public static <T> CommonResponse<T> badRequest(BaseErrorCode errorCode) {
        return new CommonResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
    public static <T> CommonResponse<T> unauthorized(BaseErrorCode errorCode) {
        return new CommonResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
    public static <T> CommonResponse<T> forbidden(BaseErrorCode errorCode) {
        return new CommonResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
    public static <T> CommonResponse<T> notFound(BaseErrorCode errorCode) {
        return new CommonResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
    public static <T> CommonResponse<T> conflict(BaseErrorCode errorCode) {
        return new CommonResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
    public static <T> CommonResponse<T> internalServerError(BaseErrorCode errorCode) {
        return new CommonResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
}
