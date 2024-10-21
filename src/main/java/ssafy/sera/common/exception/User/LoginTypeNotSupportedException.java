package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class LoginTypeNotSupportedException extends BaseException {
    public LoginTypeNotSupportedException() {
        super(PlayerErrorCode.LOGIN_TYPE_NOT_SUPPORTED);
    }
}