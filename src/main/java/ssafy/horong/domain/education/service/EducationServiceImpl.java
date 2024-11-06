package ssafy.horong.domain.education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ssafy.horong.api.education.response.EducationRecordResponse;
import ssafy.horong.api.education.response.GetEducationRecordResponse;
import ssafy.horong.api.education.response.SaveEducationResponseFromData;
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

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

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
        // 현재 로그인한 사용자 ID를 가져옵니다.
        Long userId = SecurityUtil.getLoginMemberId().orElseThrow(null);

        // 해당 사용자와 연관된 모든 교육 기록을 가져옵니다.
        List<EducationRecord> educationRecords = educationRecordRepository.findByUserId(userId);

        // Education을 기준으로 EducationRecord를 그룹화합니다.
        Map<Education, List<EducationRecordResponse>> groupedRecords = new HashMap<>();
        for (EducationRecord record : educationRecords) {
            EducationRecordResponse recordResponse = new EducationRecordResponse(
                    record.getId(),
                    record.getEducation(),
                    record.getCer(),
                    record.getGtIdx(),
                    record.getHypIdx(),
                    record.getDate(),
                    s3Util.getS3UrlFromS3(record.getAudio())
            );
            groupedRecords
                    .computeIfAbsent(record.getEducation(), k -> new ArrayList<>())
                    .add(recordResponse);
        }

        // 응답 리스트를 생성합니다.
        List<GetEducationRecordResponse> responseList = new ArrayList<>();
        for (Map.Entry<Education, List<EducationRecordResponse>> entry : groupedRecords.entrySet()) {
            GetEducationRecordResponse response = new GetEducationRecordResponse(entry.getKey(), entry.getValue());
            responseList.add(response);
        }

        return responseList;
    }

    @Transactional
    public EducationRecordResponse saveEducationRecord(SaveEduciatonRecordCommand command) {
        // Education 및 userId 가져오기
        Education education = educationRepository.findByWord(command.word());
        Long userId = SecurityUtil.getLoginMemberId().orElseThrow(null);
        UUID recordIndex = UUID.randomUUID();

        // S3에 오디오 파일 업로드
        String location = s3Util.uploadToS3(command.audio(), command.word() + "/" + userId + "/" + recordIndex, "education/");

        // 임시 값으로 EducationRecord 생성
        EducationRecord educationRecord = EducationRecord.builder()
                .education(education)
                .audio(location)
                .cer(0) // 임시 값
                .build();

        // 외부 서버 요청하여 cer 값을 받아옴
        String requestUrl = webClientProperties.url();
        SaveEducationResponseFromData dataResponse = webClient.post()
                .uri(requestUrl)
                .body(BodyInserters.fromValue(Map.of(
                        "word", command.word(),
                        "url", s3Util.getS3UrlFromS3(location)
                )))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Unknown error")
                                .flatMap(errorBody -> Mono.error(new DataNotFoundException()))
                )
                .bodyToMono(SaveEducationResponseFromData.class)
                .blockOptional()
                .orElseThrow(DataNotFoundException::new);

        // 받은 CER 값을 설정하고 DB에 저장
        educationRecord.setCer(dataResponse.cer());
        educationRecordRepository.save(educationRecord);

        // EducationRecordResponse 객체를 생성하여 반환
        return new EducationRecordResponse(
                educationRecord.getId(),
                educationRecord.getEducation(),
                educationRecord.getCer(),
                educationRecord.getGtIdx(),
                educationRecord.getHypIdx(),
                educationRecord.getDate(),
                URI.create(location)
        );
    }
}
