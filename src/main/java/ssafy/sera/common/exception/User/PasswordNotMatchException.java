package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class PasswordNotMatchException extends BaseException {
    public PasswordNotMatchException() {
        super(UserErrorCode.PASSWORD_NOT_MATCH);
    }
}
