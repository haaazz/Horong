package ssafy.horong.domain.education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ssafy.horong.api.education.response.*;
import ssafy.horong.common.exception.data.DataNotFoundException;
import ssafy.horong.common.properties.WebClientProperties;
import ssafy.horong.common.util.S3Util;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.education.command.SaveEduciatonRecordCommand;
import ssafy.horong.domain.education.entity.*;
import ssafy.horong.domain.education.repository.*;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final EducationStampRepository educationStampRepository;

    @Transactional
    public TodayWordsResponse getTodayWords() {
        LocalDateTime today = LocalDateTime.now();
        User currentUser = getCurrentUser();
        EducationDay educationDay = educationDayRepository.findTopByUserAndCreatedAtBeforeTodayOrderByDayDesc(currentUser, today)
                .orElseGet(() -> {
                    // 오늘의 EducationDay가 없을 때 가장 최근의 day 값으로 새로 생성
                    int recentDay = educationDayRepository.findTopByUserOrderByCreatedAtDesc(currentUser)
                            .map(EducationDay::getDay)
                            .orElse(0);  // 가장 최근의 값이 없으면 0으로 설정

                    // day가 0인 경우 기본값 1로 설정, 그렇지 않으면 가장 최근 값에 1을 더함
                    int newDayValue = (recentDay == 0) ? 1 : recentDay + 1;

                    // 새로운 EducationDay 생성
                    EducationDay newEducationDay = EducationDay.builder()
                            .user(currentUser)
                            .wordIds(new ArrayList<>())  // 초기 빈 단어 목록
                            .day(newDayValue)
                            .createdAt(LocalDateTime.now())
                            .build();

                    // 새로 생성한 객체를 저장하고 반환
                    return educationDayRepository.save(newEducationDay);
                });

        log.info("오늘의 단어: {}", educationDay);

        List<Education> todayWords = educationRepository.findByDay(educationDay.getDay());
        log.info("오늘의 단어: {}", todayWords);
        log.info("단어 전부: {}", educationRepository.findAll());
        User user = getCurrentUser();
        List<EducationLanguage> translatedWords = new ArrayList<>();
        for (Education education : todayWords) {
            List<EducationLanguage> words = educationLanguageRepository.findByEducationIdAndLanguage(education.getId(), user.getLanguage());
            translatedWords.addAll(words);
        }
        return new TodayWordsResponse(todayWords, translatedWords);
    }

    public GetAllEducationRecordResponse getAllEducationRecord() {
        Long userId = SecurityUtil.getLoginMemberId().orElseThrow(null);
        List<EducationRecord> educationRecords = educationRecordRepository.findByUserId(userId);

        // LocalDate와 word별로 그룹화
        Map<LocalDate, Map<String, List<EducationRecordResponse>>> groupedByDateAndWord = new HashMap<>();

        for (EducationRecord record : educationRecords) {
            EducationRecordResponse recordResponse = new EducationRecordResponse(
                    record.getId(),
                    record.getCer(),
                    record.getGtIdx(),
                    record.getHypIdx(),
                    s3Util.getS3UrlFromS3(record.getAudio())
            );

            // 날짜별로 그룹화
            groupedByDateAndWord
                    .computeIfAbsent(record.getDate(), date -> new HashMap<>())
                    .computeIfAbsent(record.getEducation().getWord(), word -> new ArrayList<>())
                    .add(recordResponse);
        }

        // Map을 List<GetEducationRecordByDayResponse>로 변환
        List<GetEducationRecordByDayResponse> dayResponses = groupedByDateAndWord.entrySet().stream()
                .map(dateEntry -> {
                    LocalDate date = dateEntry.getKey();
                    List<GetEducationRecordByWordResponse> wordResponses = dateEntry.getValue().entrySet().stream()
                            .map(wordEntry -> new GetEducationRecordByWordResponse(wordEntry.getValue(), wordEntry.getKey()))
                            .collect(Collectors.toList());
                    return new GetEducationRecordByDayResponse(wordResponses, date);
                })
                .collect(Collectors.toList());

        // 변환된 리스트를 사용하여 최종 응답 생성
        return new GetAllEducationRecordResponse(dayResponses);
    }

    @Transactional
    public EducationRecordResponse saveEducationRecord(SaveEduciatonRecordCommand command) {
        Education education = educationRepository.findByWord(command.word());
        List<Education> findAll = educationRepository.findAll();
        log.info("교육 목록: {}", findAll);
        log.info("education: {}", education);

        Long userId = SecurityUtil.getLoginMemberId().orElseThrow(null);
        UUID recordIndex = UUID.randomUUID();
        String location = s3Util.uploadToS3(command.audio(), command.word() + "/" + userId + "/" + recordIndex, "education/");
        log.info("word_id", education.getId());

        EducationRecord educationRecord = EducationRecord.builder()
                .education(education)
                .audio(location)
                .user(getCurrentUser())
                .cer(0) // 임시 값
                .build();

        String requestUrl = webClientProperties.url() + "/education";

        log.info("s3 주소 {}", s3Util.getS3UrlFromS3(location));

        URI uri = s3Util.getS3UrlFromS3(location);

        // WebClient 호출, application/octet-stream으로 수신
        SaveEducationResponseFromData response = webClient.post()
                .uri(requestUrl)
                .body(BodyInserters.fromValue(Map.of(
                        "word", command.word(),
                        "s3_url", uri
                )))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .doOnNext(errorBody -> log.error("Error response body: {}", errorBody))
                                .flatMap(errorBody -> Mono.error(new DataNotFoundException()))
                )
                .bodyToMono(SaveEducationResponseFromData.class)
                .blockOptional()
                .orElseThrow(DataNotFoundException::new);

        log.info("byteResponse: {}", response);

        educationRecord.setCer(response.cer());
        educationRecord.setGtIdx(response.gtIdx());
        educationRecord.setHypIdx(response.hypIdx());
        educationRecordRepository.save(educationRecord);

        // 기존의 EducationDay를 찾거나 오늘 날짜로 새로운 EducationDay 생성
        LocalDateTime today = LocalDateTime.now();
        User currentUser = getCurrentUser();
        EducationDay educationDay = educationDayRepository.findTopByUserAndCreatedAtBeforeTodayOrderByDayDesc(currentUser, today)
                .orElseGet(() -> {
                    // 오늘의 EducationDay가 없을 때 가장 최근의 day 값으로 새로 생성
                    int recentDay = educationDayRepository.findTopByUserOrderByCreatedAtDesc(currentUser)
                            .map(EducationDay::getDay)
                            .orElse(0);  // 가장 최근의 값이 없으면 0으로 설정

                    // day가 0인 경우 기본값 1로 설정, 그렇지 않으면 가장 최근 값에 1을 더함
                    int newDayValue = (recentDay == 0) ? 1 : recentDay + 1;

                    // 새로운 EducationDay 생성
                    EducationDay newEducationDay = EducationDay.builder()
                            .user(currentUser)
                            .wordIds(new ArrayList<>())  // 초기 빈 단어 목록
                            .day(newDayValue)
                            .createdAt(LocalDateTime.now())
                            .build();

                    // 새로 생성한 객체를 저장하고 반환
                    return educationDayRepository.save(newEducationDay);
                });

        // 오늘의 EducationDay에 단어 ID 추가
        if (!educationDay.getWordIds().contains(education.getId().intValue())) {
            educationDay.getWordIds().add(education.getId().intValue());
        }

        // 5개의 단어를 학습했는지 확인
        if (educationDay.getWordIds().size() >= 5) {
            LocalDate todayDate = LocalDate.now();

            // 오늘 날짜로 스탬프가 이미 존재하는지 확인
            boolean stampExists = educationStampRepository.existsByUserIdAndCreatedAtDateOnly(userId, todayDate);
            if (!stampExists) {
                // 스탬프가 없다면 새로 생성
                EducationStamp educationStamp = EducationStamp.builder()
                        .user(getCurrentUser())
                        .build();

                educationStampRepository.save(educationStamp);
            }
        }

        // EducationRecordResponse 생성 및 반환
        return new EducationRecordResponse(
                educationRecord.getId(),
                response.cer(),
                response.gtIdx(),
                response.hypIdx(),
                uri
        );
    }


    private User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
    }

    public List<LocalDate> getStampDates() {
        Long userId = SecurityUtil.getLoginMemberId().orElseThrow(null);
        List<EducationStamp> stamps = educationStampRepository.findLatestByUserIdWithIdEndingInZero(userId);
        return stamps.stream()
                .map(EducationStamp::getCreatedAt)
                .map(LocalDateTime::toLocalDate)
                .toList();
    }

    public EducationDay getOrCreateTodayEducationDay(User currentUser) {
        // 전체 EducationDay 리스트를 사용자 기준으로 조회, 생성일자 역순으로 정렬
        List<EducationDay> educationDays = educationDayRepository.findAllByUserOrderByCreatedAtDesc(currentUser);

        if (!educationDays.isEmpty()) {
            EducationDay lastEducationDay = educationDays.get(0);

            // 가장 최근 기록이 오늘이라면 해당 객체를 반환
            if (lastEducationDay.getCreatedAt().toLocalDate().isEqual(LocalDate.now())) {
                return lastEducationDay;
            }

            // 최근 기록이 오늘이 아닐 경우, 가장 최근의 day 값에 +1
            int newDayValue = lastEducationDay.getDay() + 1;

            // 새로운 EducationDay 객체 생성
            EducationDay newEducationDay = EducationDay.builder()
                    .user(currentUser)
                    .wordIds(new ArrayList<>()) // 빈 단어 목록으로 초기화
                    .day(newDayValue)
                    .createdAt(LocalDateTime.now())
                    .build();

            // 생성한 객체를 저장 후 반환
            return educationDayRepository.save(newEducationDay);
        } else {
            // 조회된 기록이 없다면 첫 번째 day 값으로 초기화
            EducationDay newEducationDay = EducationDay.builder()
                    .user(currentUser)
                    .wordIds(new ArrayList<>()) // 빈 단어 목록으로 초기화
                    .day(1) // 첫 번째 day 값으로 설정
                    .createdAt(LocalDateTime.now())
                    .build();

            // 생성한 객체를 저장 후 반환
            return educationDayRepository.save(newEducationDay);
        }
    }

    public EducationRecordResponse getEducationRecordDetail(Long recordId) {
        EducationRecord educationRecord = educationRecordRepository.findById(recordId).orElseThrow();

        return new EducationRecordResponse(
                educationRecord.getId(),
                educationRecord.getCer(),
                educationRecord.getGtIdx(),
                educationRecord.getHypIdx(),
                s3Util.getS3UrlFromS3(educationRecord.getAudio())
        );
    }

}
