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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShortFormServiceImpl implements ShortFormService {
    private final WebClient webClient;
    private final WebClientProperties webClientProperties;

    public List<ShortFromListResponse> getShortFormList() {
        // 로그인된 사용자의 ID 가져오기
        Long playerId = SecurityUtil.getLoginMemberId().orElseThrow(DataNotFoundException::new);

        // 요청 URL 생성
        String requestUrl = webClientProperties.url() + "/shortForm/" + playerId;

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
}
