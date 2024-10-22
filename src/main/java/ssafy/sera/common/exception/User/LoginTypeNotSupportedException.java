package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class LoginTypeNotSupportedException extends BaseException {
    public LoginTypeNotSupportedException() {
        super(UserErrorCode.LOGIN_TYPE_NOT_SUPPORTED);
    }
}