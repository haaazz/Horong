package ssafy.horong.api.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.api.member.request.PasswordUpdateRequest;
import ssafy.horong.api.member.request.UserSignupRequest;
import ssafy.horong.api.member.request.UserUpdateRequest;
import ssafy.horong.api.member.response.UserDetailResponse;
import ssafy.horong.api.member.response.UserIdResponse;
import ssafy.horong.api.member.response.UserProfileDetailResponse;
import ssafy.horong.api.member.response.UserSignupResponse;
import ssafy.horong.domain.member.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "회원관리")
public class MemberController {
    private final UserService userService;

    @Operation(summary = "회원가입", description = """
         소셜로그인 시 저장된 임시 회원 정보를 정식 회원으로 업데이트하는 API입니다.
         이 과정을 통해 해당 회원은 임시 회원이 아닌 정식 회원으로 전환됩니다.
     """)
    @PostMapping(value = "/signup", consumes = { "multipart/form-data" })
    public CommonResponse<UserSignupResponse> signup(@ModelAttribute @Validated UserSignupRequest request) {
        log.info("[UserController] 회원가입 >>>> request: {}", request);
        UserSignupResponse response = userService.signupMember(request.toCommand());
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "회원 탈퇴", description = "회원 정보를 삭제하는 API입니다.")
    @PatchMapping
    public CommonResponse<?> deleteMember() {
        log.info("[UserController] 회원 탈퇴");
        String message = userService.deleteMember();
        return CommonResponse.ok(message, null);
    }

    @Operation(summary = "닉네임 중복 조회", description = "닉네임 중복 조회하는 API입니다.")
    @GetMapping("/nickname")
    public CommonResponse<String> checkNickname(@RequestParam String nickname) {
        log.info("[UserController] 닉네임 중복 조회 >>>> nickname: {}", nickname);
        boolean isDuplicated = userService.checkNickname(nickname);
        String message = isDuplicated ? "이미 사용중인 닉네임입니다." : "사용 가능한 닉네임입니다.";
        return CommonResponse.ok(message, null);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "로그인 시 회원 정보 조회", description = "로그인 시 회원 정보를 조회하는 API입니다.")
    @GetMapping
    public CommonResponse<UserDetailResponse> getMemberDetail() {
        log.info("[UserController] 로그인 시 회원 정보 조회");
        UserDetailResponse response = userService.getMemberDetail();
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "프로필에서 회원 정보 조회", description = "프로필에서 회원 정보를 조회하는 API입니다.")
    @GetMapping("/profile")
    public CommonResponse<UserProfileDetailResponse> getMemberProfileDetail() {
        log.info("[UserController] 프로필에서 회원 정보 조회");
        UserProfileDetailResponse response = userService.getMemberProfileDetail();
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정하는 API입니다.")
    @PutMapping(value = "/profile", consumes = { "multipart/form-data" })
    public CommonResponse<UserDetailResponse> updateMemberProfile(@ModelAttribute @Validated UserUpdateRequest request) {
        log.info("[UserController] 회원 정보 수정 >>>> request: {}", request);
        UserDetailResponse response = userService.updateMemberProfile(request.toCommand());
        return CommonResponse.ok(response);
    }

    @Operation(summary = "회원 비밀번호 수정", description = "회원 비밀번호를 수정하는 API입니다.")
    @PatchMapping(value = "/password")
    public CommonResponse<String> updateMemberPassword(@RequestBody PasswordUpdateRequest request) {
        log.info("[UserController] 회원 비밀번호 수정 >>>> request: {}", request);
        userService.updateMemberPassword(request.toCommand());
        return CommonResponse.ok("비밀번호가 성공적으로 변경되었습니다.", null);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "플레이어의 ID 조회", description = "나의 id를 조회하는 API입니다.")
    @GetMapping("/id")
    public CommonResponse<UserIdResponse> getUserId() {
        log.info("[UserController] 플레이어의 ID 조회");
        UserIdResponse response = userService.getMemberId();
        return CommonResponse.ok(response);
    }
}
