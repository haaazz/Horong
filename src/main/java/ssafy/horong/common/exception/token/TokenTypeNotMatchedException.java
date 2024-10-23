package ssafy.horong.common.exception.token;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.TokenErrorCode;

public class TokenTypeNotMatchedException extends BaseException {
    public TokenTypeNotMatchedException() {
        super(TokenErrorCode.NOT_MATCHED_TOKEN_TYPE);
    }
}
