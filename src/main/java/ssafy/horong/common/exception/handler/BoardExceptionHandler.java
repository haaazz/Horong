package ssafy.horong.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.exception.Board.NotAdminExeption;
import ssafy.horong.common.exception.Board.NotAuthenticatedException;

@RestControllerAdvice
@Slf4j
public class BoardExceptionHandler {

    @ExceptionHandler(NotAdminExeption.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse handleNotAdminExeption(NotAdminExeption e) {
        log.error("NotAdminExeption", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }

    @ExceptionHandler(NotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse handleNotAuthenticatedException(NotAuthenticatedException e) {
        log.error("NotAuthenticatedException", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }
}
