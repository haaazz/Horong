package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class PostNotFoundException extends BaseException {
    public PostNotFoundException() {
        super(CommunityErrorCode.POST_NOT_FOUND);
    }
}
