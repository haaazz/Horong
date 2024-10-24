package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.BoardErrorCode;

public class NotAuthenticatedException extends BaseException {
    public NotAuthenticatedException() {
        super(BoardErrorCode.NOT_AUTHENTICATED);
    }
}
