package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class KakaoMailUserNotFoundException extends BaseException {
    public KakaoMailUserNotFoundException() {
        super(UserErrorCode.KAKAO_MAIL_USER_NOT_FOUND);
    }
}
