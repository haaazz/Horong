package ssafy.sera.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static ssafy.sera.api.StatusCode.*;

@Getter
@AllArgsConstructor
public enum PlayerErrorCode implements BaseErrorCode {
    EMAIL_DUPLICATE(BAD_REQUEST, "PLAYER_400_1", "이미 존재하는 이메일입니다."),
    NICKNAME_DUPLICATE(BAD_REQUEST, "PLAYER_400_2", "이미 존재하는 닉네임입니다."),
    PLAYER_DUPLICATE(BAD_REQUEST, "PLAYER_400_3", "이미 존재하는 회원입니다."),
    MESSAGE_SENDING(BAD_REQUEST, "PLAYER_400_4", "메시지 전송에 실패하였습니다."),
    PASSWORD_NOT_MATCH(NOT_FOUND, "PLAYER_400_5", "비밀번호가 일치하지 않습니다."),

    INVALID_LOGIN_INFO(UNAUTHORIZED, "PLAYER_401_1", "로그인 유저가 존재하지 않습니다."),

    PLAYER_NOT_FOUND(NOT_FOUND, "PLAYER_404_1", "존재하지 않는 회원입니다."),
    TEMP_PLAYER_NOT_FOUND(NOT_FOUND, "PLAYER_404_2", "로그인 과정에서 생성된 임시 회원 정보가 존재하지 않습니다."),
    PROFILE_NOT_FOUND(NOT_FOUND, "PLAYER_404_3", "프로필 이미지를 찾을 수 없습니다."),
    VERIFICATION_FAILURE(NOT_FOUND, "PLAYER_404_4", "인증에 실패하였습니다."),
    EMAIL_NOT_FOUND(NOT_FOUND, "PLAYER_404_5", "존재하지 않는 이메일입니다."),
    PHONE_NUMBER_NOT_FOUND(NOT_FOUND, "PLAYER_404_6", "존재하지 않는 전화번호입니다."),
    KAKAO_MAIL_PLAYER_NOT_FOUND(NOT_FOUND, "PLAYER_404_&", "카카오 메일에 해당하는 회원을 찾을 수 없습니다."),
    LOGIN_TYPE_NOT_SUPPORTED(NOT_FOUND, "PLAYER_404_8", "지원하지 않는 로그인 타입입니다."),

    PLAYER_ALREADY_DELETED(CONFLICT, "PLAYER_409_1", "이미 삭제된 회원입니다."),
    PLAYER_ALREADY_BOOKED(CONFLICT, "PLAYER_409_2", "같은 시간에 다른 경기가 이미 예약된 선수입니다."),

    ABNORMAL_LOGIN_PROGRESS(INTERNAL_SERVER_ERROR, "PLAYER_500_1", "비정상적으로 로그인이 진행되었습니다.");

    private final Integer httpStatus;
    private final String code;
    private final String message;
}
