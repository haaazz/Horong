package ssafy.horong.common.exception.horongChat;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.HorongChatErrorCode;

public class ChatroomNotAuthenticatedException extends BaseException {
    public ChatroomNotAuthenticatedException() {
        super(HorongChatErrorCode.CHATROOM_NOT_AUTHENTICATED);
    }

}
