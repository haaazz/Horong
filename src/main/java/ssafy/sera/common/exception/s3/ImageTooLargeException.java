package ssafy.sera.common.exception.s3;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.S3ErrorCode;

public class ImageTooLargeException extends BaseException {
    public ImageTooLargeException() {
        super(S3ErrorCode.IMAGE_TOO_LARGE);
    }
}
