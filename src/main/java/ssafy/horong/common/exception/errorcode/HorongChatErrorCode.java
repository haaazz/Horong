package ssafy.horong.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HorongChatErrorCode implements BaseErrorCode{

    CHATROOM_NOT_FOUND(404, "CHATROOM_404_1", "채팅방을 찾을 수 없습니다."),
    CHAT_NOT_FOUND(404, "CHAT_404_2", "채팅을 찾을 수 없습니다."),
    CHATROOM_NOT_AUTHENTICATED(401, "CHATROOM_401_1", "채팅방에 접근 권한이 없습니다.");

    private final Integer httpStatus; // HTTP 상태 코드
    private final String code;          // 에러 코드
    private final String message;       // 에러 메시지
}