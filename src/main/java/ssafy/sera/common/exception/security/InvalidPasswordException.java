package ssafy.sera.common.exception.security;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.SecurityErrorCode;

public class InvalidPasswordException extends BaseException {
    public InvalidPasswordException() {
        super(SecurityErrorCode.INVALID_PASSWORD);
    }
}
