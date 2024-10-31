package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class PasswordNotValidExeption extends BaseException {
    public PasswordNotValidExeption() {
        super(UserErrorCode.PASSWORD_NOT_VALID);
    }
}
