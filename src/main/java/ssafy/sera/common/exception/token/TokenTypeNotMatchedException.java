package ssafy.sera.common.exception.token;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.TokenErrorCode;

public class TokenTypeNotMatchedException extends BaseException {
    public TokenTypeNotMatchedException() {
        super(TokenErrorCode.NOT_MATCHED_TOKEN_TYPE);
    }
}
