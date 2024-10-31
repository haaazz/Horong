package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class NicknameNotValidExeption extends BaseException {
    public NicknameNotValidExeption() {
        super(UserErrorCode.NICKNAME_NOT_VALID);
    }
}
