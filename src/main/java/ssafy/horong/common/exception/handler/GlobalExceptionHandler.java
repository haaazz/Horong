package ssafy.horong.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.exception.global.AccessDeniedRequestException;
import ssafy.horong.common.exception.User.InvalidLoginInfoException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidLoginInfoException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse handleInvalidLoginInfoException(InvalidLoginInfoException e) {
        log.error("InvalidLoginInfoException Error", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }

    @ExceptionHandler(AccessDeniedRequestException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResponse handleAccessDeniedRequestException(AccessDeniedRequestException e) {
        log.error("AccessDeniedRequestException Error", e);
        return CommonResponse.forbidden(e.getErrorCode());
    }

}
