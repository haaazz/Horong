package ssafy.horong.api.shortForm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.api.shortForm.request.ModifyIsSavedRequest;
import ssafy.horong.api.shortForm.request.ModifyPreferenceRequest;
import ssafy.horong.api.shortForm.request.SaveShortFormLogRequest;
import ssafy.horong.api.shortForm.response.ShortFromListResponse;
import ssafy.horong.api.shortForm.response.ShortFromResponse;
import ssafy.horong.domain.shortForm.command.ModifyIsSavedCommand;
import ssafy.horong.domain.shortForm.command.SaveShortFormLogCommand;
import ssafy.horong.domain.shortForm.command.ModifyPreferenceCommand;
import ssafy.horong.domain.shortForm.service.ShortFormService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shortForm")
@Slf4j
@Tag(name = "ShortForm", description = "숏폼 관련 API")
public class ShortFromController {

    private final ShortFormService shortFormService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "숏폼 리스트 조회", description = "로그인한 사용자의 숏폼 리스트를 조회합니다.")
    @GetMapping("")
    public CommonResponse<List<ShortFromResponse>> getShortFormList() {
        List<ShortFromResponse> response = shortFormService.getShortFormList();
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "숏폼 리스트 조회", description = "로그인한 사용자의 숏폼 리스트를 조회합니다.")
    @GetMapping("preferences")
    public CommonResponse<List<ShortFromResponse>> getPreferenceList() {
        List<ShortFromResponse> response = shortFormService.getPreferenceList();
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "숏폼 리스트 조회", description = "로그인한 사용자의 숏폼 리스트를 조회합니다.")
    @GetMapping("liked")
    public CommonResponse<List<ShortFromResponse>> getLikedList() {
        List<ShortFromResponse> response = shortFormService.getLikedList();
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "숏폼 detail 조회", description = "숏폼의 상세 정보를 조회합니다.")
    @GetMapping("/{shortFormId}")
    public CommonResponse<ShortFromListResponse> getShortFormDetail(@PathVariable Long shortFormId) {
        ShortFromListResponse response = shortFormService.getShortFormDetail(shortFormId);
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "숏폼 로그 저장", description = "사용자의 숏폼 시청 로그를 저장합니다.")
    @PostMapping("/log")
    public CommonResponse<String> saveShortFormLog(@RequestBody SaveShortFormLogRequest request) {
        String response = shortFormService.saveShortFormLog(request.toCommand());
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "숏폼 좋아요/싫어요 수정", description = "사용자가 숏폼에 대해 좋아요 또는 싫어요를 수정합니다.")
    @PostMapping("/preference")
    public CommonResponse<String> modifyPreference(@RequestBody ModifyPreferenceRequest request) {
        String response = shortFormService.modifyPreference(request.toCommand());
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "숏폼 스크랩 여부 수정", description = "사용자가 숏폼의 스크랩 여부를 수정합니다.")
    @PostMapping("/is_saved")
    public CommonResponse<String> modifyIsSaved(@RequestBody ModifyIsSavedRequest request) {
        String response = shortFormService.modifyIsSaved(request.toCommand());
        return CommonResponse.ok(response);
    }
}