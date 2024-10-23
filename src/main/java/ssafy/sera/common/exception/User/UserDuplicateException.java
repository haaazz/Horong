package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class UserDuplicateException extends BaseException {
    public UserDuplicateException() {
        super(UserErrorCode.USER_DUPLICATE);
    }
}
