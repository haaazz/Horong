package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class EmailNotFoundException extends BaseException {
    public EmailNotFoundException() {
        super(PlayerErrorCode.EMAIL_NOT_FOUND);
    }
}
