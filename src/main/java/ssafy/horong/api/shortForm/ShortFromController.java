package ssafy.horong.api.shortForm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.api.shortForm.response.ShortFromListResponse;
import ssafy.horong.domain.shortForm.service.ShortFormService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shortForm")
@Slf4j
@Tag(name = "ShortForm", description = "숏폼")
public class ShortFromController {

    private final ShortFormService shortFormService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "숏폼 리스트 조회", description = "숏폼 리스트를 조회하는 API입니다.")
    public CommonResponse<List<ShortFromListResponse>> getShortFormList() {
        List<ShortFromListResponse> response = shortFormService.getShortFormList();
        return CommonResponse.ok(response);
    }
}
