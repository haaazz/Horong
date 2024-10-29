package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.BoardErrorCode;
import ssafy.horong.common.exception.errorcode.S3ErrorCode;

public class NotAdminExeption extends BaseException {
    public NotAdminExeption() {
        super(BoardErrorCode.NOT_ADMIN);
    }
}
