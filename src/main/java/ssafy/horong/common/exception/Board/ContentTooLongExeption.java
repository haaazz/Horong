package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class ContentTooLongExeption extends BaseException {
    public ContentTooLongExeption() {
        super(CommunityErrorCode.CONTENT_TOO_LONG);
    }
}
