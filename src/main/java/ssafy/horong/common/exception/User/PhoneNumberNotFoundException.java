package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class PhoneNumberNotFoundException extends BaseException {
    public PhoneNumberNotFoundException() {
        super(UserErrorCode.PHONE_NUMBER_NOT_FOUND);
    }
}
