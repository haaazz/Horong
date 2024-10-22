package ssafy.sera.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.sera.api.auth.request.TokenRefreshRequest;
import ssafy.sera.api.auth.response.AuthResponse;
import ssafy.sera.common.exception.User.PasswordNotMatchException;
import ssafy.sera.common.exception.token.TokenSaveFailedException;
import ssafy.sera.common.kakao.client.KakaoOauthClient;
import ssafy.sera.common.properties.KakaoLoginProperties;
import ssafy.sera.common.util.JwtParser;
import ssafy.sera.common.util.JwtProcessor;
import ssafy.sera.domain.auth.command.LoginCommand;
import ssafy.sera.domain.auth.model.DecodedJwtToken;
import ssafy.sera.domain.auth.model.LoginToken;
import ssafy.sera.domain.member.entity.User;
import ssafy.sera.domain.member.repository.UserRepository;
import ssafy.sera.common.exception.User.*;

import ssafy.sera.common.exception.security.*;

import java.util.Optional;

import static ssafy.sera.common.constant.redis.KEY_PREFIX.REFRESH_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final JwtParser jwtParser;
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