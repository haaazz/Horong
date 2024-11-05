package ssafy.horong.api.education;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.api.education.request.SaveEduciatonRecordRequest;
import ssafy.horong.api.education.response.TodayWordsResponse;
import ssafy.horong.api.member.request.UserSignupRequest;
import ssafy.horong.domain.education.service.EducationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/education")
@Tag(name = "education", description = "한국어학습")
public class EducationController {
    private final EducationService educationService;
    @Operation(summary = "오늘의 단어", description = "오늘의 단어를 가져오는 API입니다.")
    @GetMapping("/today")
    public CommonResponse<?> getTodayWords() {
        TodayWordsResponse response = educationService.getTodayWords();
        return CommonResponse.ok(response);
    }

    @Operation(summary = "교육 기록 조회", description = "모든 교육 기록을 조회하는 API입니다.")
    @GetMapping("/records")
    public CommonResponse<?> getEducationRecord() {
        return CommonResponse.ok(educationService.getAllEducationRecord());
    }

    @Operation(summary = "한국어 학습 기록", description = "한국어 학습 기록을 저장하는 API입니다.")
    @PostMapping(value = "/record", consumes = { "multipart/form-data" })
    public CommonResponse<?> saveEducationRecord(@ModelAttribute @Validated SaveEduciatonRecordRequest request) {
        return CommonResponse.ok(educationService.saveEducationRecord(request.toCommand()));
    }
}
