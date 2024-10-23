package ssafy.horong.common.exception.s3;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.S3ErrorCode;

public class PresignedUrlGenerationFailException extends BaseException {
    public PresignedUrlGenerationFailException() {
        super(S3ErrorCode.PERSIGNEDURL_GENERATION_FAILED);
    }
}
