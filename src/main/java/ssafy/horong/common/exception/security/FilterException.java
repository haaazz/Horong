package ssafy.horong.common.exception.security;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.SecurityErrorCode;

public class FilterException extends BaseException {
    public FilterException() {
        super(SecurityErrorCode.FILTER_ERROR);
    }
}
