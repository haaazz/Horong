package ssafy.sera.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.sera.api.auth.request.TokenRefreshRequest;
import ssafy.sera.api.auth.response.AuthResponse;
import ssafy.sera.common.exception.User.LoginTypeNotSupportedException;
import ssafy.sera.common.exception.User.PasswordNotMatchException;
import ssafy.sera.common.exception.token.TokenSaveFailedException;
import ssafy.sera.common.kakao.client.KakaoOauthClient;
import ssafy.sera.common.kakao.model.KakaoToken;
import ssafy.sera.common.properties.KakaoLoginProperties;
import ssafy.sera.common.util.JwtParser;
import ssafy.sera.common.util.JwtProcessor;
import ssafy.sera.domain.auth.command.KakaoLoginCommand;
import ssafy.sera.domain.auth.command.LoginCommand;
import ssafy.sera.domain.auth.model.DecodedJwtToken;
import ssafy.sera.domain.auth.model.LoginToken;
import ssafy.sera.domain.auth.model.UserInfo;
import ssafy.sera.domain.member.common.MemberRole;
import ssafy.sera.domain.member.entity.User;
import ssafy.sera.domain.member.repository.UserRepository;
import ssafy.sera.common.exception.User.*;
import ssafy.sera.domain.member.common.Member;
import java.util.Optional;
import ssafy.sera.common.exception.security.*;

import static ssafy.sera.common.constant.redis.KEY_PREFIX.REFRESH_TOKEN;
import static ssafy.sera.common.constant.security.LOGIN_TYPE.BASIC;
import static ssafy.sera.common.constant.security.LOGIN_TYPE.KAKAO;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final KakaoOauthClient kakaoOauthClient;
    private final KakaoLoginProperties kakaoLoginProperties;
    private final JwtParser jwtParser;
    private final JwtProcessor jwtProcessor;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    @Override
    @Transactional
    public AuthResponse login(LoginCommand command) {
        log.info("[AuthService] 로그인 >>>> 로그인 타입: {}, 이메일: {}", command.loginType(), command.email());
        LoginToken tokens;

        switch (command.loginType()) {
            case KAKAO:
                KakaoLoginCommand kakaoLoginCommand = getKakaoLoginCommand(command.authCode());
                UserInfo userInfo = jwtParser.getUserInfo(kakaoLoginCommand);

                if (isNotRegisteredPlayer(userInfo)) {
                    return AuthResponse.notRegistered(userInfo);
                }

                log.debug("[AuthService] 가입된 멤버 정보 확인 >>>> 이름: {}, 이메일: {}", userInfo.nickname(), userInfo.email());
                User kakaoMember = userRepository.findNotDeletedUserByEmail(userInfo.email())
                        .orElseThrow(KakaoMailUserNotFoundException::new);

                if (kakaoMember.getIsDeleted()) {
                    return AuthResponse.notRegistered(userInfo);
                }

                tokens = generateTokens(kakaoMember);
                jwtProcessor.saveRefreshToken(tokens);
                break;

            case BASIC:
                String email = command.email();
                String password = command.password();

                Member basicMember = findMemberByEmail(email)
                        .orElseThrow(EmailNotFoundException::new);

                if (!passwordEncoder.matches(password, basicMember.getPassword())) {
                    throw new PasswordNotMatchException();
                }

                tokens = generateTokens(basicMember);
                jwtProcessor.saveRefreshToken(tokens);
                break;

            default:
                throw new LoginTypeNotSupportedException();
        }

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
        Member member = findMemberById(decodedJwtToken)
                .orElseThrow(InvalidTokenException::new);
        try {
            String newAccessToken = jwtProcessor.generateAccessToken(member);
            String newRefreshToken = jwtProcessor.generateRefreshToken(member);
            jwtProcessor.renewRefreshToken(request.refreshToken(), newRefreshToken, member);
            return AuthResponse.of(newAccessToken, newRefreshToken);
        }
        catch (Exception e) {
            log.error("[AuthService] Access 토큰 발급 중 오류 발생", e);
            throw new TokenSaveFailedException();
        }

    }

    private boolean isNotRegisteredPlayer(UserInfo userInfo) {
        log.debug("[AuthService] 미가입 유저 확인 >>>> 유저 이메일: {}", userInfo.email());
        String email = userInfo.email();
        Member member = findMemberByEmail(email).orElse(null);

        if (member == null){
            userRepository.save(User.createTempPlayer());
            return true;
        }
        return isTempPlayer(member);
    }

    private Optional<Member> findMemberByEmail(String email) {
        return userRepository.findNotDeletedUserByEmail(email).map(member -> (Member) member);
    }

    private LoginToken generateTokens(Member member) {
        String accessToken = jwtProcessor.generateAccessToken(member);
        String refreshToken = jwtProcessor.generateRefreshToken(member);
        return new LoginToken(accessToken, refreshToken);
    }

    private Boolean isTempPlayer(Member member) {
        return member.getRole() == MemberRole.TEMP;
    }

    private Optional<Member> findMemberById(DecodedJwtToken decodedJwtToken) {
        String role = decodedJwtToken.role();
        Long id = decodedJwtToken.memberId();
        log.debug("[AuthService] 멤버 ID 조회 >>>> 역할: {}, ID: {}", role, id);

        return switch (role) {
            case "PLAYER" -> userRepository.findById(id)
                    .map(member -> (Member) member);
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }

    private KakaoLoginCommand getKakaoLoginCommand(String code) {
        log.debug("[AuthService] 카카오 로그인 토큰 발급 >>>> 인증 코드: {}", code);
        KakaoToken kakaoToken = getKakaoToken(code);
        log.debug("[AuthService] 카카오 로그인 토큰 발급 성공 >>>> 카카오 토큰: {}", kakaoToken);
        return KakaoLoginCommand.byKakao(kakaoToken, kakaoOauthClient.getPublicKeys(), jwtParser, kakaoLoginProperties);
    }

    private KakaoToken getKakaoToken(String code) {
        return kakaoOauthClient.getToken(
                kakaoLoginProperties.contentType(),
                kakaoLoginProperties.clientId(),
                kakaoLoginProperties.loginRedirectUri(),
                code,
                kakaoLoginProperties.clientSecret()
        );
    }
}