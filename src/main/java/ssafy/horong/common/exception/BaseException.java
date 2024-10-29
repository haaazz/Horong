package ssafy.horong.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ssafy.horong.common.exception.errorcode.BaseErrorCode;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private final BaseErrorCode errorCode;
}
