package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class PlayerAlreadyDeletedException extends BaseException {
    public PlayerAlreadyDeletedException() {
        super(PlayerErrorCode.PLAYER_ALREADY_DELETED);
    }
}
