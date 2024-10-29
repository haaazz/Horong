package ssafy.horong.common.exception.security;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.SecurityErrorCode;

public class PasswordUsedException extends BaseException {
    public PasswordUsedException() {super (SecurityErrorCode.PASSWORD_USED);}

}
