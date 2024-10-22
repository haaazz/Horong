package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class VerificationException extends BaseException {
    public VerificationException() {
        super(UserErrorCode.VERIFICATION_FAILURE);
    }
}
