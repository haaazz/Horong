package ssafy.horong.common.exception.s3;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.S3ErrorCode;

public class ExtensionNotAllowedException extends BaseException {
    public ExtensionNotAllowedException() {
        super(S3ErrorCode.EXTENSION_NOT_ALLOWED);
    }
}