package ssafy.horong.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.exception.User.*;

@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {
    @ExceptionHandler(AbnormalLoginProgressException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handleAbnormalLoginProgressException(AbnormalLoginProgressException e) {
        log.error("AbnormalLoginProgressException Error", e);
        return CommonResponse.internalServerError(e.getErrorCode());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleNotFoundMemberException(MemberNotFoundException e) {
        log.error("MemberNotFoundException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(UserAlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse handleMemberAlreadyDeletedException(UserAlreadyDeletedException e) {
        log.error("UserAlreadyDeletedException Error", e);
        return CommonResponse.conflict(e.getErrorCode());
    }

    @ExceptionHandler(VerificationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleSmsVerificationException(VerificationException e) {
        log.error("VerificationException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleEmailNotFoundException(EmailNotFoundException e) {
        log.error("EmailNotFoundException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handlePasswordNotMatchException(PasswordNotMatchException e) {
        log.error("PasswordNotMatchException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(UserIdDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse handleEmailDuplicateException(UserIdDuplicateException e) {
        log.error("UserIdDuplicateException Error", e);
        return CommonResponse.conflict(e.getErrorCode());
    }

    @ExceptionHandler(NickNameDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse handleNickNameDuplicateException(NickNameDuplicateException e) {
        log.error("NickNameDuplicateException Error", e);
        return CommonResponse.conflict(e.getErrorCode());
    }

    @ExceptionHandler(PasswordNotValidExeption.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handlePasswordNotValidExeption(PasswordNotValidExeption e) {
        log.error("PasswordNotValidExeption Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(UserIdNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleUserIdNotValidException(UserIdNotValidException e) {
        log.error("UserIdNotValidException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(NicknameNotValidExeption.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleNicknameNotValidExeption(NicknameNotValidExeption e) {
        log.error("NicknameNotValidExeption Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

}
