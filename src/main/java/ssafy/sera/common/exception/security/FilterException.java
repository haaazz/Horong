package ssafy.sera.common.exception.security;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.SecurityErrorCode;

public class FilterException extends BaseException {
    public FilterException() {
        super(SecurityErrorCode.FILTER_ERROR);
    }
}
