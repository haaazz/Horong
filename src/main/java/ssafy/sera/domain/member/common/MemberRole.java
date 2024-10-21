package ssafy.sera.domain.member.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    PLAYER("선수"), TEMP("임시 유저");
    private final String value;


    public static MemberRole fromValue(String role) {
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        for (MemberRole memberRole : MemberRole.values()) {
            if (memberRole.name().equalsIgnoreCase(role) || memberRole.getValue().equals(role)) {
                return memberRole;
            }
        }
        throw new IllegalArgumentException("해당하는 회원 역할이 없습니다: " + role);
    }
}