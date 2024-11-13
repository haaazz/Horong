package ssafy.horong.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.exception.horongChat.ChatroomNotAuthenticatedException;
import ssafy.horong.common.exception.s3.ExtensionNotAllowedException;

@Slf4j
@RestControllerAdvice
public class HorongChatHandler {

    @ExceptionHandler(ChatroomNotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse handleChatroomNotAuthenticatedException(ChatroomNotAuthenticatedException e) {
        log.error("handleChatroomNotAuthenticatedException", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }
}
