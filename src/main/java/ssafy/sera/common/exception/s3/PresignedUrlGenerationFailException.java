package ssafy.sera.common.exception.s3;

import ssafy.sera.common.exception.BaseException;
import ssafy.sera.common.exception.errorcode.S3ErrorCode;

public class PresignedUrlGenerationFailException extends BaseException {
    public PresignedUrlGenerationFailException() {
        super(S3ErrorCode.PERSIGNEDURL_GENERATION_FAILED);
    }
}
