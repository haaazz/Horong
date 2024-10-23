package ssafy.sera.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.sera.api.CommonResponse;
import ssafy.sera.common.exception.User.*;

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

    @ExceptionHandler(TempUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleNotFoundTempUser(TempUserNotFoundException e) {
        log.error("TempUserNotFoundException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(UserAlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse handleMemberAlreadyDeletedException(UserAlreadyDeletedException e) {
        log.error("UserAlreadyDeletedException Error", e);
        return CommonResponse.conflict(e.getErrorCode());
    }

    @ExceptionHandler(MessageSendingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleMessageSendingException(MessageSendingException e) {
        log.error("MessageSendingException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
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

    @ExceptionHandler(PhoneNumberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handlePhoneNumberNotFoundException(PhoneNumberNotFoundException e) {
        log.error("PhoneNumberNotFoundException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(KakaoMailUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleKakaoMailUserNotFoundException(KakaoMailUserNotFoundException e) {
        log.error("KakaoMailUserNotFoundException Error", e);
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

    @ExceptionHandler(UserAlreadyBookedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<?> handleUserAlreadyBookedException(UserAlreadyBookedException e) {
        log.error("UserAlreadyBookedException occurs", e);
        return CommonResponse.conflict(e.getErrorCode());
    }
}
