package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class UserAlreadyBookedException extends BaseException {
    public UserAlreadyBookedException() {
        super(UserErrorCode.USER_ALREADY_BOOKED);
    }
}
