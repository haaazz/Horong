package ssafy.horong.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.auth.request.TokenRefreshRequest;
import ssafy.horong.api.auth.response.AuthResponse;
import ssafy.horong.common.exception.User.PasswordNotMatchException;
import ssafy.horong.common.exception.token.TokenSaveFailedException;
import ssafy.horong.common.util.JwtParser;
import ssafy.horong.common.util.JwtProcessor;
import ssafy.horong.domain.auth.command.LoginCommand;
import ssafy.horong.domain.auth.model.DecodedJwtToken;
import ssafy.horong.domain.auth.model.LoginToken;
import ssafy.horong.domain.member.common.CustomUserDetails;
import ssafy.horong.domain.member.common.MemberRole;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;
import ssafy.horong.common.exception.User.*;

import ssafy.horong.common.exception.security.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static ssafy.horong.common.constant.redis.KEY_PREFIX.REFRESH_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final JwtProcessor jwtProcessor;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    @Override
    @Transactional
    public AuthResponse login(LoginCommand command) {

        LoginToken tokens;
        String userId = command.userId();
        String password = command.password();

        User user = findMemberByUserId(userId)
                .orElseThrow(EmailNotFoundException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException();
        }

        Authentication newAuthentication = SecurityContextHolder.getContext().getAuthentication(); // 기본값으로 초기화


        if (user.getRole() == MemberRole.ADMIN) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 기존 권한을 가져옴
            Collection<GrantedAuthority> currentAuthorities = new ArrayList<>(authentication.getAuthorities());

            // 새로운 권한 추가
            currentAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            // 새로운 Authentication 객체 생성 (기존 인증 정보 사용)
            newAuthentication = new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    authentication.getCredentials(),
                    currentAuthorities
            );

            // 새로운 Authentication 객체를 SecurityContext에 설정
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        }

        log.info("권한 확인 {}", newAuthentication);


        tokens = generateTokens(user);
        jwtProcessor.saveRefreshToken(tokens);

        return AuthResponse.of(tokens.accessToken(), tokens.refreshToken());
    }

    @Override
    @Transactional
    public void logout() {
        log.info("[AuthService] 로그아웃");
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            jwtProcessor.expireToken(token);
        } else {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }
    }

    @Override
    @Transactional
    public AuthResponse refresh(TokenRefreshRequest request) {
        log.info("[AuthService] Access 토큰 발급 >>>> Refresh 토큰: {}", request.refreshToken());
        DecodedJwtToken decodedJwtToken = jwtProcessor.decodeToken(request.refreshToken(), REFRESH_TOKEN);
        User user = findMemberById(decodedJwtToken)
                .orElseThrow(InvalidTokenException::new);
        try {
            String newAccessToken = jwtProcessor.generateAccessToken(user);
            String newRefreshToken = jwtProcessor.generateRefreshToken(user);
            jwtProcessor.renewRefreshToken(request.refreshToken(), newRefreshToken, user);
            return AuthResponse.of(newAccessToken, newRefreshToken);
        }
        catch (Exception e) {
            log.error("[AuthService] Access 토큰 발급 중 오류 발생", e);
            throw new TokenSaveFailedException();
        }

    }

    private Optional<User> findMemberByUserId(String userId) {
        return userRepository.findNotDeletedUserByUserId(userId);
    }

    private LoginToken generateTokens(User member) {
        String accessToken = jwtProcessor.generateAccessToken(member);

        String refreshToken = jwtProcessor.generateRefreshToken(member);
        return new LoginToken(accessToken, refreshToken);
    }

    private Optional<User> findMemberById(DecodedJwtToken decodedJwtToken) {
        Long id = decodedJwtToken.memberId();
        return userRepository.findById(id);
    }
}