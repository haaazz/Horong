package ssafy.horong.common.exception.s3;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.S3ErrorCode;

public class ProfileNotFoundInS3Exception extends BaseException {
    public ProfileNotFoundInS3Exception() {
        super(S3ErrorCode.PROFILE_NOT_FOUND_IN_S3);
    }
}
