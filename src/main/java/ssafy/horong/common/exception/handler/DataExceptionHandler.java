package ssafy.horong.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.exception.data.DataNotFoundException;

@Slf4j
@RestControllerAdvice
public class DataExceptionHandler {
    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleDataNotFoundException(DataNotFoundException e) {
        log.error("PDataNotFoundException Error", e);
        return CommonResponse.notFound(e.getErrorCode());
    }
}
