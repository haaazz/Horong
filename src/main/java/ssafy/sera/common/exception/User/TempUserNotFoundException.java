package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class TempUserNotFoundException extends BaseException {
    public TempUserNotFoundException() {
        super(UserErrorCode.TEMP_USER_NOT_FOUND);
    }
}
