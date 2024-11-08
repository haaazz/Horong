package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class NotAdminExeption extends BaseException {
    public NotAdminExeption() {
        super(CommunityErrorCode.NOT_ADMIN);
    }
}
