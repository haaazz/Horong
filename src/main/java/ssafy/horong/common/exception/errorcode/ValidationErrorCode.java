package ssafy.horong.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ValidationErrorCode implements BaseErrorCode{
    METHOD_ARGUMENT_INVALID(400, "VALID_400_1", "유효하지 않은 메소드 인자입니다.");

    private final Integer httpStatus;
    private final String code;
    private final String message;
}
