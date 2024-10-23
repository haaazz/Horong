package ssafy.horong.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.api.auth.request.LoginRequest;
import ssafy.horong.api.auth.request.TokenRefreshRequest;
import ssafy.horong.api.auth.response.AuthResponse;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.auth.service.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class authController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "유저 정보를 이용하여 login type으로 로그인 합니다.")
    @PostMapping("/login")
    public CommonResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("[AuthController] 로그인 >>>> 로그인 타입: {}, 이메일: {}", request.loginType(), request.email());
        AuthResponse loginResponse = authService.login(request.toCommand());
        return CommonResponse.ok(loginResponse);
    }

    @Operation(summary = "토큰 갱신", description = "refresh token으로 access token을 갱신합니다.")
    @PostMapping("/refresh")
    public CommonResponse<AuthResponse> refresh(@RequestBody TokenRefreshRequest request) {
        log.info("[AuthController] Access 토큰 갱신");
        AuthResponse rotateTokenResponse = authService.refresh(request);
        return CommonResponse.ok(rotateTokenResponse);
    }

    @Operation(summary = "로그아웃", description = "엑세스 토큰을 이용하여 login type으로 로그인 합니다.")
    @PostMapping("/logout")
    public CommonResponse<?> logout() {
        log.info("[AuthController] 로그아웃");
        authService.logout();
        return CommonResponse.noContent();
    }

    @Operation(summary = "ping", description = "인증 서버가 정상적으로 동작하는지 확인합니다.")
    @GetMapping("/ping")
    public CommonResponse<?> ping() {
        log.info("[AuthController] 인증 확인 >>>> 로그인 멤버 ID : {}", SecurityUtil.getLoginMemberId());
        return CommonResponse.ok("pong", null);
    }
}
