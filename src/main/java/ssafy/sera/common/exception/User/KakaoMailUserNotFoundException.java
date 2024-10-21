package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class KakaoMailUserNotFoundException extends BaseException {
    public KakaoMailUserNotFoundException() {
        super(PlayerErrorCode.KAKAO_MAIL_PLAYER_NOT_FOUND);
    }
}
