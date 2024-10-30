package ssafy.horong.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardErrorCode implements BaseErrorCode {

    NOT_AUTHENTICATED(401, "BOARD_401_1", "삭제/수정 권한이 없습니다."),
    CONTENT_TOO_LONG(400, "BOARD_400_2", "게시글은 1000자 이하로 작성해야합니다."),
    COMMENT_TOO_LONG(400, "BOARD_400_3", "댓글은 1000자 이하로 작성해야합니다."),

    POST_NOT_FOUND(404, "BOARD_404_1", "게시글을 찾을 수 없습니다."),
    POST_DELETED(404, "BOARD_404_2", "삭제된 게시글입니다."),

    NOT_ADMIN(403, "BOARD_403_1", "관리자만 접근 가능합니다.");



    private final Integer httpStatus;
    private final String code;
    private final String message;
}