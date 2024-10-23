package ssafy.horong.common.exception.security;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.SecurityErrorCode;

public class InvalidPasswordException extends BaseException {
    public InvalidPasswordException() {
        super(SecurityErrorCode.INVALID_PASSWORD);
    }
}
