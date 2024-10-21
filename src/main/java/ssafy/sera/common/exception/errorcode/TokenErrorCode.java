package ssafy.sera.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenErrorCode implements BaseErrorCode {
    EXPIRED_REFRESH_TOKEN(401, "REFRESH_TOKEN_401_1", "해당 리프레쉬토큰은 만료되었습니다."),
    NOT_MATCHED_TOKEN_TYPE(400, "TOKEN_400_1", "토큰 타입이 일치하지 않습니다."),
    TOKEN_SAVE_FAILED(500, "TOKEN_500_1", "토큰을 레디스에 저장하는데 실패했습니다."),
    ;

    private final Integer httpStatus;
    private final String code;
    private final String message;
}
