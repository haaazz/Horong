package ssafy.sera.common.exception.token;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.SecurityErrorCode;

public class RefreshTokenExpiredException extends BaseException {
    public RefreshTokenExpiredException() {
        super(SecurityErrorCode.TOKEN_EXPIRED);
    }
}
