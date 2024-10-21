package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class PlayerAlreadyBookedException extends BaseException {
    public PlayerAlreadyBookedException() {
        super(PlayerErrorCode.PLAYER_ALREADY_BOOKED);
    }
}
