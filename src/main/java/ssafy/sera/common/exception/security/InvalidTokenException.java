package ssafy.sera.common.exception.security;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.SecurityErrorCode;

public class InvalidTokenException extends BaseException {

        public InvalidTokenException() {
            super(SecurityErrorCode.INVALID_TOKEN);
        }
}
