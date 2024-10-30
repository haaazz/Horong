package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.BoardErrorCode;

public class PostNotFoundException extends BaseException {
    public PostNotFoundException() {
        super(BoardErrorCode.POST_NOT_FOUND);
    }
}
