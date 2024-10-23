package ssafy.horong.common.exception.s3;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.S3ErrorCode;

public class ImageTooLargeException extends BaseException {
    public ImageTooLargeException() {
        super(S3ErrorCode.IMAGE_TOO_LARGE);
    }
}
