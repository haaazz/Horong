package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class MessageSendingException extends BaseException {
    public MessageSendingException() {
        super(UserErrorCode.MESSAGE_SENDING);
    }
}
