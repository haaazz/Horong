package ssafy.horong.common.exception.security;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.SecurityErrorCode;

public class AlreadyUsedPasswordException extends BaseException {
    public AlreadyUsedPasswordException() {
        super(SecurityErrorCode.ALDREADY_USED_PASSWORD);
    }
}
