package ssafy.sera.common.exception.handler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ssafy.sera.common.exception.token.TokenSaveFailedException;

@RequiredArgsConstructor
@Slf4j
public class TokenExceptionHandler {
    @ExceptionHandler(TokenSaveFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleTokenGenerationFailedException(TokenSaveFailedException e) {
        log.error("TokenSaveFailedException Error", e);
    }
}
