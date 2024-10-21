package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class MessageSendingException extends BaseException {
    public MessageSendingException() {
        super(PlayerErrorCode.MESSAGE_SENDING);
    }
}
