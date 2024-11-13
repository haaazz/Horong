package ssafy.horong.domain.shortForm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ssafy.horong.api.shortForm.response.ShortFromListResponse;
import ssafy.horong.common.exception.data.DataNotFoundException;
import ssafy.horong.common.properties.WebClientProperties;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.common.util.UserUtil;
import ssafy.horong.domain.shortForm.command.ModifyIsSavedCommand;
import ssafy.horong.domain.shortForm.command.SaveShortFormLogCommand;
import ssafy.horong.domain.shortForm.command.ModifyPreferenceCommand;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShortFormServiceImpl implements ShortFormService {
    private final WebClient webClient;
    private final WebClientProperties webClientProperties;
    private final UserUtil userUtil;

    public List<ShortFromListResponse> getShortFormList() {
        // 로그인된 사용자의 ID 가져오기
        Long userId = userUtil.getCurrentUser().getId();

        // 요청 URL 생성
        String requestUrl = webClientProperties.url() + "/shortform/" + userId;

        // WebClient 호출
        List<ShortFromListResponse> response = webClient.get()
                .uri(requestUrl)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Unknown error")
                                .flatMap(errorBody -> Mono.error(new DataNotFoundException()))
                )
                .bodyToFlux(ShortFromListResponse.class)
                .collectList()
                .blockOptional()
                .orElseThrow(DataNotFoundException::new);

        log.info("response: {}", response);

        return response;
    }

    public String saveShortFormLog(SaveShortFormLogCommand command) {
        // 요청 URL 생성
        String requestUrl = webClientProperties.url() + "/shortform/log";
        Long userId = userUtil.getCurrentUser().getId();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 요청 바디 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("shortform_id", command.shortFormId());
        requestBody.put("user_id", userId);
        requestBody.put("start_at", command.startAt().format(formatter)); // 날짜 형식 변환
        requestBody.put("end_at", command.endAt().format(formatter));     // 날짜 형식 변환

        log.info("로그 저장 요청: {}", requestBody);

        // WebClient 호출
        String response = webClient.post()
                .uri(requestUrl)
                .body(Mono.just(requestBody), Map.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Unknown error")
                                .flatMap(errorBody -> Mono.error(new DataNotFoundException()))
                )
                .bodyToMono(String.class)
                .block();

        log.info("로그 저장 응답: {}", response);
        return "로그 저장에 성공했습니다.";
    }

    // 숏폼 좋아요/싫어요 수정
    public String modifyPreference(ModifyPreferenceCommand command) {
        // 요청 URL 생성
        String requestUrl = webClientProperties.url() + "/shortform/preference";

        // 요청 바디 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("shortform_id", command.shortFormId());
        requestBody.put("user_id", userUtil.getCurrentUser().getId());
        requestBody.put("preference", command.preference());
        log.info("좋아요/싫어요 수정 요청: {}", requestBody);

        // WebClient 호출
        String response = webClient.post()
                .uri(requestUrl)
                .body(Mono.just(requestBody), Map.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Unknown error")
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody)))
                )
                .bodyToMono(String.class)
                .block();

        log.info("좋아요/싫어요 수정 응답: {}", response);
        return "좋아요/싫어요 반영에 성공했습니다.";
    }

    // 숏폼 스크랩 여부 수정
    public String modifyIsSaved(ModifyIsSavedCommand command) {

        // 요청 URL 생성
        String requestUrl = webClientProperties.url() + "/shortform/is_saved";

        // 요청 바디 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("shortform_id", command.shortFormId());
        requestBody.put("user_id", userUtil.getCurrentUser().getId());
        requestBody.put("is_saved", command.isSaved());

        log.info("스크랩 여부 수정 요청: {}", requestBody);

        // WebClient 호출
        String response = webClient.post()
                .uri(requestUrl)
                .body(Mono.just(requestBody), Map.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Unknown error")
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody)))
                )
                .bodyToMono(String.class)
                .block();

        log.info("스크랩 여부 수정 응답: {}", response);
        return "스크랩 반영에 성공했습니다.";
    }
}