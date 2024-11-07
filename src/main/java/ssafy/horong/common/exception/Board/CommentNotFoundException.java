package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.BoardErrorCode;

public class CommentNotFoundException extends BaseException {
    public CommentNotFoundException() {
        super(BoardErrorCode.COMMENT_NOT_FOUND);
    }
}
