package ssafy.sera.common.exception.security;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.SecurityErrorCode;

public class PasswordUsedException extends BaseException {
    public PasswordUsedException() {super (SecurityErrorCode.PASSWORD_USED);}

}
