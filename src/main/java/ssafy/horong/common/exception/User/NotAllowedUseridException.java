package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class NotAllowedUseridException extends BaseException {
    public NotAllowedUseridException() {
        super(UserErrorCode.NOT_ALLOWED_USERID);
    }
}
