package ssafy.horong.domain.member.common;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public enum MemberRole {
    USER("유저"),
    TEMP("임시 유저"),
    ADMIN("관리자", List.of(USER));

    private final String value;
    private final List<MemberRole> inheritedRoles;

    // 단일 파라미터 생성자
    MemberRole(String value) {
        this(value, Collections.emptyList()); // 기본적으로 빈 리스트로 초기화
    }

    // 두 개의 파라미터를 받는 생성자
    MemberRole(String value, List<MemberRole> inheritedRoles) {
        this.value = value;
        this.inheritedRoles = inheritedRoles;
    }

    // 역할 확인 메소드
    public boolean hasRole(MemberRole role) {
        return this == role || inheritedRoles.contains(role);
    }

    // 역할을 문자열로부터 변환하는 메소드
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
