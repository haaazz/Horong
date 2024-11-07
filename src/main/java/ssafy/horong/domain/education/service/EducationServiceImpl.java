package ssafy.horong.domain.education.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
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
import ssafy.horong.domain.education.entity.EducationDay;
import ssafy.horong.domain.education.entity.EducationLanguage;
import ssafy.horong.domain.education.entity.EducationRecord;
import ssafy.horong.domain.education.repository.EducationDayRepository;
import ssafy.horong.domain.education.repository.EducationLanguageRepository;
import ssafy.horong.domain.education.repository.EducationRecordRepository;
import ssafy.horong.domain.education.repository.EducationRepository;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;
import java.nio.charset.StandardCharsets;

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
    private final EducationDayRepository educationDayRepository;

    public TodayWordsResponse getTodayWords() {
        List<Education> todayWords = educationRepository.findByPublishDate(LocalDate.now());
        User user = userRepository.findByUserId(SecurityUtil.getLoginMemberId().toString()).orElseThrow(() -> new RuntimeException("User not found"));
        List<EducationLanguage> translatedWords = educationLanguageRepository.findByEducationIdAndLanguage(todayWords.get(0).getId(), user.getLanguage());
        return new TodayWordsResponse(todayWords, translatedWords);
    }

    public List<GetEducationRecordResponse> getAllEducationRecord() {
        Long userId = SecurityUtil.getLoginMemberId().orElseThrow(null);
        List<EducationRecord> educationRecords = educationRecordRepository.findByUserId(userId);
        Map<Education, List<EducationRecordResponse>> groupedRecords = new HashMap<>();

        for (EducationRecord record : educationRecords) {
            EducationRecordResponse recordResponse = new EducationRecordResponse(
                    record.getId(),
                    record.getEducation().getWord(),
                    record.getCer(),
                    record.getGtIdx(),
                    record.getHypIdx(),
                    record.getDate(),
                    s3Util.getS3UrlFromS3(record.getAudio())
            );
            groupedRecords.computeIfAbsent(record.getEducation(), k -> new ArrayList<>()).add(recordResponse);
        }

        List<GetEducationRecordResponse> responseList = new ArrayList<>();
        for (Map.Entry<Education, List<EducationRecordResponse>> entry : groupedRecords.entrySet()) {
            responseList.add(new GetEducationRecordResponse(entry.getKey(), entry.getValue()));
        }
        return responseList;
    }

    @Transactional
    public EducationRecordResponse saveEducationRecord(SaveEduciatonRecordCommand command) {
        Education education = educationRepository.findByWord(command.word());
        Long userId = SecurityUtil.getLoginMemberId().orElseThrow(null);
        UUID recordIndex = UUID.randomUUID();
        String location = s3Util.uploadToS3(command.audio(), command.word() + "/" + userId + "/" + recordIndex, "education/");
        EducationRecord educationRecord = EducationRecord.builder()
                .education(education)
                .audio(location)
                .user(getCurrentUser())
                .cer(0) // 임시 값
                .build();

        String requestUrl = webClientProperties.url() + "/word";

        log.info("s3 주소 {}", s3Util.getS3UrlFromS3(location));

        // WebClient 호출, application/octet-stream으로 수신
        SaveEducationResponseFromData response = webClient.post()
                .uri(requestUrl)
                .body(BodyInserters.fromValue(Map.of(
                        "word", command.word(),
                        "s3_url", s3Util.getS3UrlFromS3(location)
                )))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .doOnNext(errorBody -> log.error("Error response body: {}", errorBody)) // 에러 내용 로그 출력
                                .flatMap(errorBody -> Mono.error(new DataNotFoundException())) // errorBody를 사용해 예외 생성
                )
                .bodyToMono(SaveEducationResponseFromData.class)
                .blockOptional()
                .orElseThrow(DataNotFoundException::new);

        log.info("byteResponse: {}", response);

        educationRecord.setCer(response.cer());
        educationRecordRepository.save(educationRecord);

        EducationDay educationDay = educationDayRepository.findByUserId(userId).orElseThrow(null);
        educationDay.setDay(education.getDay());

        if (!educationDay.getWordIds().contains(education.getId().intValue())) {
            educationDay.getWordIds().add(education.getId().intValue());
        }

        educationDayRepository.save(educationDay);

        return new EducationRecordResponse(
                educationRecord.getId(),
                educationRecord.getEducation().getWord(),
                educationRecord.getCer(),
                educationRecord.getGtIdx(),
                educationRecord.getHypIdx(),
                educationRecord.getDate(),
                URI.create(location)
        );
    }

    // byte[] 데이터를 SaveEducationResponseFromData로 변환하는 메서드
    private SaveEducationResponseFromData convertToSaveEducationResponseFromData(byte[] byteResponse) {
        try {
            String jsonString = new String(byteResponse, StandardCharsets.UTF_8); // byte[]를 문자열로 변환
            return new ObjectMapper().readValue(jsonString, SaveEducationResponseFromData.class); // JSON 파싱
        } catch (Exception e) {
            throw new RuntimeException("데이터 변환 실패", e);
        }
    }
    private User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
    }
}
