package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class InvalidLoginInfoException extends BaseException {
    public InvalidLoginInfoException() {
        super(UserErrorCode.INVALID_LOGIN_INFO);
    }
}
