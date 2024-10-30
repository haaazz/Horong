package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.BoardErrorCode;

public class ContentTooLongExeption extends BaseException {
    public ContentTooLongExeption() {
        super(BoardErrorCode.CONTENT_TOO_LONG);
    }
}
