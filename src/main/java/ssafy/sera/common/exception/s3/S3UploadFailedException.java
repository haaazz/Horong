package ssafy.sera.common.exception.s3;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.S3ErrorCode;

public class S3UploadFailedException extends BaseException {
    public S3UploadFailedException() {
        super(S3ErrorCode.S3_IMAGE_UPLOAD_FAILED);
    }
}
