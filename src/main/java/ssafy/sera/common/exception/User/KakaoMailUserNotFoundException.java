package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class KakaoMailUserNotFoundException extends BaseException {
    public KakaoMailUserNotFoundException() {
        super(UserErrorCode.KAKAO_MAIL_USER_NOT_FOUND);
    }
}
