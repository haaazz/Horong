package ssafy.horong.common.exception.data;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.DataErrorCode;

public class DataNotFoundException extends BaseException {
    public DataNotFoundException() {
        super(DataErrorCode.DATA_NOT_FOUND);
    }
}
