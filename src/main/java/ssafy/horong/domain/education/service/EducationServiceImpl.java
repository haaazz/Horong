package ssafy.horong.domain.education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ssafy.horong.api.education.response.GetEducationRecordResponse;
import ssafy.horong.api.education.response.TodayWordsResponse;
import ssafy.horong.common.exception.data.DataNotFoundException;
import ssafy.horong.common.properties.WebClientProperties;
import ssafy.horong.common.util.S3Util;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.education.command.SaveEduciatonRecordCommand;
import ssafy.horong.domain.education.entity.Education;
import ssafy.horong.domain.education.entity.EducationLanguage;
import ssafy.horong.domain.education.entity.EducationRecord;
import ssafy.horong.domain.education.repository.EducationLanguageRepository;
import ssafy.horong.domain.education.repository.EducationRecordRepository;
import ssafy.horong.domain.education.repository.EducationRepository;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationServiceImpl implements EducationService {
    private final EducationRepository educationRepository;
    private final UserRepository userRepository;
    private final EducationLanguageRepository educationLanguageRepository;
    private final EducationRecordRepository educationRecordRepository;
    private final S3Util s3Util;
    private final WebClient webClient;
    private final WebClientProperties webClientProperties;

    public TodayWordsResponse getTodayWords() {
        List<Education> todayWords = educationRepository.findByPublishDate(LocalDate.now());

        User user = userRepository.findByUserId(SecurityUtil.getLoginMemberId().toString()).orElseThrow(() -> new RuntimeException("User not found"));

        List<EducationLanguage> translatedWords = educationLanguageRepository.findByEducationIdAndLanguage(todayWords.get(0).getId(), user.getLanguage());

        return new TodayWordsResponse(todayWords, translatedWords);
    }

    public List<GetEducationRecordResponse> getAllEducationRecord() {
        // 교육 기록 리스트를 가져옵니다.
        List<Education> educationList = educationRepository.findAll();

        // 응답 리스트를 빈 리스트로 초기화합니다.
        List<GetEducationRecordResponse> responseList = new ArrayList<>();

        // 현재 로그인한 사용자 ID를 가져옵니다.
        Long userId = SecurityUtil.getLoginMemberId().orElseThrow(null);

        // 각 교육 자료에 대해 교육 기록을 조회하고 응답 리스트에 추가합니다.
        for (Education education : educationList) {
            // 교육 기록 리스트를 가져옵니다.
            List<EducationRecord> educationRecords = educationRecordRepository.findByEducationIdAndUserId(education.getId(), userId);

            // 교육 기록이 존재하는 경우, 응답 리스트에 추가합니다.
            for (EducationRecord educationRecord : educationRecords) {
                GetEducationRecordResponse response = new GetEducationRecordResponse(education, educationRecords);
                responseList.add(response);
            }
        }
        return responseList;
    }

    @Transactional
    public float saveEducationRecord(SaveEduciatonRecordCommand command) {
        // Education 객체를 가져옵니다.
        Education education = educationRepository.findByWord(command.word());

        // Education ID와 현재 사용자 ID를 사용하여 EducationRecord를 찾습니다.
        Long educationId = education.getId(); // Education의 ID를 가져옵니다.
        Long userId = SecurityUtil.getLoginMemberId().orElseThrow(null); // 현재 로그인한 사용자의 ID를 가져옵니다.
        List<EducationRecord> records = educationRecordRepository.findByEducationIdAndUserId(educationId, userId);

        // records가 null이면 0으로 설정합니다.
        int recordIndex = (records != null && !records.isEmpty()) ? records.size() - 1 : 0;

        // S3에 업로드합니다.
        String location = s3Util.uploadToS3(command.audio(), command.word() + "/" + userId + "/" + recordIndex, "education/");

        // EducationRecord를 저장합니다.
        EducationRecord educationRecord = EducationRecord.builder()
                .education(education)
                .audio(location)
                .build();
        educationRecordRepository.save(educationRecord); // EducationRecord 저장

        String requestUrl = webClientProperties.url();

        float score = webClient.get()
                .uri(requestUrl)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Unknown error")
                                .flatMap(errorBody -> Mono.error(new DataNotFoundException()))
                )
                .bodyToMono(float.class)
                .blockOptional()
                .orElseThrow(DataNotFoundException::new);

        educationRecord.setCer(score);

        // 서버 요청 후 float 값을 반환합니다.
        return score;
    }
}
