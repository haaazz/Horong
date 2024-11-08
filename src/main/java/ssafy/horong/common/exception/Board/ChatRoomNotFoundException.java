package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class ChatRoomNotFoundException extends BaseException {
    public ChatRoomNotFoundException() {
        super(CommunityErrorCode.CHATROOM_NOT_FOUND);
    }
}
