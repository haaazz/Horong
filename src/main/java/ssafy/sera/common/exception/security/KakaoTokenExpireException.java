package ssafy.sera.common.exception.security;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.SecurityErrorCode;

public class KakaoTokenExpireException extends BaseException {
    public KakaoTokenExpireException() {
        super(SecurityErrorCode.KAKAO_TOKEN_EXPIRE);
    }
}
