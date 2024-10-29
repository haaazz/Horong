package ssafy.horong.common.exception.validation;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.ValidationErrorCode;

public class MethodArgumentInvalidException extends BaseException {
    public MethodArgumentInvalidException() {
        super(ValidationErrorCode.METHOD_ARGUMENT_INVALID);
    }
}
