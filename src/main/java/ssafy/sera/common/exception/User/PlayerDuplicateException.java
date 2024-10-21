package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class PlayerDuplicateException extends BaseException {
    public PlayerDuplicateException() {
        super(PlayerErrorCode.PLAYER_DUPLICATE);
    }
}
