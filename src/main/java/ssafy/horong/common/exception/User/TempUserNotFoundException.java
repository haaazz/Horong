package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class TempUserNotFoundException extends BaseException {
    public TempUserNotFoundException() {
        super(UserErrorCode.TEMP_USER_NOT_FOUND);
    }
}
