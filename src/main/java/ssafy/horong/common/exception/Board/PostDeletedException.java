package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class PostDeletedException extends BaseException {
    public PostDeletedException() {
        super(CommunityErrorCode.POST_DELETED);
    }
}
