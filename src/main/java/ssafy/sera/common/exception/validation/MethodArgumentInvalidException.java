package ssafy.sera.common.exception.validation;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.ValidationErrorCode;

public class MethodArgumentInvalidException extends BaseException {
    public MethodArgumentInvalidException() {
        super(ValidationErrorCode.METHOD_ARGUMENT_INVALID);
    }
}
