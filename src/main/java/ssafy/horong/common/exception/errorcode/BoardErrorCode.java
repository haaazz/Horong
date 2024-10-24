package ssafy.horong.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardErrorCode implements BaseErrorCode {

    NOT_AUTHENTICATED(401, "BOARD_401_1", "삭제/수정 권한이 없습니다."),

    NOT_ADMIN(403, "BOARD_403_1", "관리자만 접근 가능합니다.");



    private final Integer httpStatus;
    private final String code;
    private final String message;
}