package ssafy.horong.common.exception.security;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.SecurityErrorCode;

public class IssuerTokenIncorrectException extends BaseException {
    public IssuerTokenIncorrectException() {
        super(SecurityErrorCode.ISSUER_TOKEN_INCORRECT);
    }
}
