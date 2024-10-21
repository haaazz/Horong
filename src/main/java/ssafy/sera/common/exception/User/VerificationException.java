package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class VerificationException extends BaseException {
    public VerificationException() {
        super(PlayerErrorCode.VERIFICATION_FAILURE);
    }
}
