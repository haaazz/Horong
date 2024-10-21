package ssafy.sera.common.exception.token;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.TokenErrorCode;

public class TokenSaveFailedException extends BaseException {
        public TokenSaveFailedException() {
            super(TokenErrorCode.TOKEN_SAVE_FAILED);
        }
}
