package ssafy.horong.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.exception.Board.*;

@RestControllerAdvice
@Slf4j
public class CommunityExceptionHandler {

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

    @ExceptionHandler(ContentTooLongExeption.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleContentTooLongExeption(ContentTooLongExeption e) {
        log.error("ContentTooLongExeption", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handlePostNotFoundException(PostNotFoundException e) {
        log.error("PostNotFoundException", e);
        return CommonResponse.notFound(e.getErrorCode());
    }

    @ExceptionHandler(PostDeletedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handlePostDeletedException(PostDeletedException e) {
        log.error("PostDeletedException", e);
        return CommonResponse.notFound(e.getErrorCode());
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleCommentNotFoundException(CommentNotFoundException e) {
        log.error("CommentNotFoundException", e);
        return CommonResponse.notFound(e.getErrorCode());
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleChatRoomNotFoundException(ChatRoomNotFoundException e) {
        log.error("ChatRoomNotFoundException", e);
        return CommonResponse.notFound(e.getErrorCode());
    }
}
