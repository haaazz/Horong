package ssafy.sera.common.exception.security;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.SecurityErrorCode;

public class NotAuthenticatedException extends BaseException {
        public NotAuthenticatedException() {
            super(SecurityErrorCode.NOT_AUTHENTICATED_ERROR);
        }
}
