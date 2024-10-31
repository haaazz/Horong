package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class LanguageNotValidExeption extends BaseException {
    public LanguageNotValidExeption() {
        super(UserErrorCode.LANGUAGE_NOT_VALID);
    }
}
