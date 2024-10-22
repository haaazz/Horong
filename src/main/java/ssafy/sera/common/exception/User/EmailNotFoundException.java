package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class EmailNotFoundException extends BaseException {
    public EmailNotFoundException() {
        super(UserErrorCode.EMAIL_NOT_FOUND);
    }
}
