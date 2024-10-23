package ssafy.horong.common.exception.security;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.SecurityErrorCode;

public class KakaoTokenExpireException extends BaseException {
    public KakaoTokenExpireException() {
        super(SecurityErrorCode.KAKAO_TOKEN_EXPIRE);
    }
}
