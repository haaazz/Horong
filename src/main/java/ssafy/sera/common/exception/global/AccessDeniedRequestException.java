package ssafy.sera.common.exception.global;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.GlobalErrorCode;

public class AccessDeniedRequestException extends BaseException {
        public AccessDeniedRequestException() {
            super(GlobalErrorCode.ACCESS_DENIED_REQUEST);
        }
}
