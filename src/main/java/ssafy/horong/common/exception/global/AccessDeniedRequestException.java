package ssafy.horong.common.exception.global;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.GlobalErrorCode;

public class AccessDeniedRequestException extends BaseException {
        public AccessDeniedRequestException() {
            super(GlobalErrorCode.ACCESS_DENIED_REQUEST);
        }
}
