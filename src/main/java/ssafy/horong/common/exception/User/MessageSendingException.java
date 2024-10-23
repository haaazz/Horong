package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class MessageSendingException extends BaseException {
    public MessageSendingException() {
        super(UserErrorCode.MESSAGE_SENDING);
    }
}
