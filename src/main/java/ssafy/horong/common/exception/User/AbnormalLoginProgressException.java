package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class AbnormalLoginProgressException extends BaseException {
    // 이메일이 있다는 것을 검증한 이후에 이메일로 유저를 조회했는데, 조회 시점에 유저가 없다면 발생하는 예외
    public AbnormalLoginProgressException() {
        super(UserErrorCode.ABNORMAL_LOGIN_PROGRESS);
    }
}
