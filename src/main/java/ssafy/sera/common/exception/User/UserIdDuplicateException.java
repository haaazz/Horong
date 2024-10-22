package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class UserIdDuplicateException extends BaseException {
    public UserIdDuplicateException() {
        super(UserErrorCode.EMAIL_DUPLICATE);
    }
}
