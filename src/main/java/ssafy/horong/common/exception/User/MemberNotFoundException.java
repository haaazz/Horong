package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class MemberNotFoundException extends BaseException {
    public MemberNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
