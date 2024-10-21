package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class MemberNotFoundException extends BaseException {
    public MemberNotFoundException() {
        super(PlayerErrorCode.PLAYER_NOT_FOUND);
    }
}
