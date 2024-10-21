package ssafy.sera.common.exception.s3;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.S3ErrorCode;

public class ExtensionNotAllowedException extends BaseException {
    public ExtensionNotAllowedException() {
        super(S3ErrorCode.EXTENSION_NOT_ALLOWED);
    }
}