package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class UserAlreadyBookedException extends BaseException {
    public UserAlreadyBookedException() {
        super(UserErrorCode.USER_ALREADY_BOOKED);
    }
}
