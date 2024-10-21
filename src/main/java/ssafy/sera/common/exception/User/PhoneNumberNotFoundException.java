package ssafy.sera.common.exception.User;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.PlayerErrorCode;

public class PhoneNumberNotFoundException extends BaseException {
    public PhoneNumberNotFoundException() {
        super(PlayerErrorCode.PHONE_NUMBER_NOT_FOUND);
    }
}
