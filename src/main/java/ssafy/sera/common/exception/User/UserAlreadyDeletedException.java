package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class UserAlreadyDeletedException extends BaseException {
    public UserAlreadyDeletedException() {
        super(UserErrorCode.USER_ALREADY_DELETED);
    }
}
