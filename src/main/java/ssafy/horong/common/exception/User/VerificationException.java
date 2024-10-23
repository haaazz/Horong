package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class VerificationException extends BaseException {
    public VerificationException() {
        super(UserErrorCode.VERIFICATION_FAILURE);
    }
}
