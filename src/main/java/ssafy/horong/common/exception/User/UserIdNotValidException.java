package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class UserIdNotValidException extends BaseException {
    public UserIdNotValidException() {
        super(UserErrorCode.USERID_NOT_VALID);
    }
}
