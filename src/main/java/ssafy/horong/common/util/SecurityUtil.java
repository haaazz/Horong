package ssafy.horong.common.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ssafy.horong.domain.member.common.MemberRole;

import java.util.Collection;
import java.util.Optional;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Slf4j
public class SecurityUtil {

    public static Optional<Long> getLoginMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof Long memberId)) {
            return Optional.empty();
        }
        return Optional.of(memberId);
    }

    public static Optional<MemberRole> getLoginMemberRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (authorities.isEmpty()) {
            return Optional.empty();
        }

        String role = authorities.iterator().next().getAuthority();
        log.info("authorities: {}", authorities);
        log.info("memberRole: {}", role);

        return Optional.of(MemberRole.fromValue(role));
    }
}
