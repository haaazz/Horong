package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class UserIdDuplicateException extends BaseException {
    public UserIdDuplicateException() {
        super(UserErrorCode.USERID_DUPLICATE);
    }
}
