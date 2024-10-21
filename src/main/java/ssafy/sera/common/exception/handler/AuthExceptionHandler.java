package ssafy.sera.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.sera.api.CommonResponse;
import ssafy.sera.common.exception.User.AbnormalLoginProgressException;
import ssafy.sera.common.exception.security.*;
import ssafy.sera.common.exception.token.TokenTypeNotMatchedException;

@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler {
    @ExceptionHandler(IssuerTokenIncorrectException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse handleIncorrectIssuerTokenException(IssuerTokenIncorrectException e) {
        log.error("IssuerTokenIncorrectException", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse handleExpiredTokenException(TokenExpiredException e) {
        log.error("TokenExpiredException", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }

    @ExceptionHandler(TokenTypeNotMatchedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleNotMatchedTokenTypeException(TokenTypeNotMatchedException e) {
        log.error("TokenTypeNotMatchedException", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }

    @ExceptionHandler(FilterException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handleFilterErrorException(FilterException e) {
        log.error("FilterException", e);
        return CommonResponse.internalServerError(e.getErrorCode());
    }

    @ExceptionHandler(AbnormalLoginProgressException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handleAbnormalLoginProgressException(AbnormalLoginProgressException e) {
        log.error("AbnormalLoginProgressException", e);
        return CommonResponse.internalServerError(e.getErrorCode());
    }

    @ExceptionHandler(KakaoTokenExpireException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handleKakaoTokenExpireException(KakaoTokenExpireException e) {
        log.error("KakaoTokenExpireException", e);
        return CommonResponse.internalServerError(e.getErrorCode());
    }

    @ExceptionHandler(NotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse handleNotAuthenticatedException(NotAuthenticatedException e) {
        log.error("NotAuthenticatedException", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }

}
