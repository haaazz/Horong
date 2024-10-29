package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class NickNameDuplicateException extends BaseException {
    public NickNameDuplicateException() {
        super(UserErrorCode.NICKNAME_DUPLICATE);
    }
}
