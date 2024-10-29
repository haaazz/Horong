package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class UserAlreadyDeletedException extends BaseException {
    public UserAlreadyDeletedException() {
        super(UserErrorCode.USER_ALREADY_DELETED);
    }
}
