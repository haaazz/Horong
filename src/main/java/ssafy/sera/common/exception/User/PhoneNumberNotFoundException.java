package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.UserErrorCode;

public class PhoneNumberNotFoundException extends BaseException {
    public PhoneNumberNotFoundException() {
        super(UserErrorCode.PHONE_NUMBER_NOT_FOUND);
    }
}
