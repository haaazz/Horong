package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.BaseErrorCode;
import ssafy.horong.common.exception.errorcode.BoardErrorCode;

public class PostDeletedException extends BaseException {
    public PostDeletedException() {
        super(BoardErrorCode.POST_DELETED);
    }
}
