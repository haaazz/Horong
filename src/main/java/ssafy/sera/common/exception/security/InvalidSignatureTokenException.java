package ssafy.sera.common.exception.security;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.SecurityErrorCode;

public class InvalidSignatureTokenException extends BaseException {
    public InvalidSignatureTokenException() {
        super(SecurityErrorCode.INVALID_SIGNATURE_TOKEN);
    }
}
