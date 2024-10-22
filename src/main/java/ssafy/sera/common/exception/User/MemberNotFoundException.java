package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class MemberNotFoundException extends BaseException {
    public MemberNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
