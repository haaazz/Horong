package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class ForbiddenWordContainedException extends BaseException {
    public ForbiddenWordContainedException() {
        super(UserErrorCode.FORBIDDEN_WORD_CONTAINED);
    }
}
