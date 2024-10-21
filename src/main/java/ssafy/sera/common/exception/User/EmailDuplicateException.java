package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class EmailDuplicateException extends BaseException {
    public EmailDuplicateException() {
        super(PlayerErrorCode.EMAIL_DUPLICATE);
    }
}
