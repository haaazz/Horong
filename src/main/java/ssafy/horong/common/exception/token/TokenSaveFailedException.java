package ssafy.horong.common.exception.token;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.TokenErrorCode;

public class TokenSaveFailedException extends BaseException {
        public TokenSaveFailedException() {
            super(TokenErrorCode.TOKEN_SAVE_FAILED);
        }
}
