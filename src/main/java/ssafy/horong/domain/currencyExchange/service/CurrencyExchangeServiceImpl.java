package ssafy.horong.domain.currencyExchange.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ssafy.horong.api.CurrencyExchange.response.CurrencyExchangeResponse;
import ssafy.horong.domain.currencyExchange.entity.CurrencyExchange;
import ssafy.horong.domain.currencyExchange.repository.CurrencyExchangeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private final CurrencyExchangeRepository currencyExchangeRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY = "currencyExchange:list";

    // Wrapper 클래스 추가
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyExchangeResponseWrapper {
        private List<CurrencyExchangeResponse> exchanges;
    }

    @Override
    @Scheduled(cron = "0 0 9,12,15,18 * * *")
    public List<CurrencyExchangeResponse> getCurrencyExchangeList() {
        // 캐시에서 데이터 조회
        try {
            CurrencyExchangeResponseWrapper wrapper = (CurrencyExchangeResponseWrapper)
                    redisTemplate.opsForValue().get(CACHE_KEY);

            if (wrapper != null) {
                return wrapper.getExchanges();
            }
        } catch (Exception e) {
            log.error("Redis 캐시 조회 실패", e);
        }

        // DB에서 데이터 조회
        List<CurrencyExchange> currencyExchangeList = currencyExchangeRepository.findAll();

        List<CurrencyExchangeResponse> response = currencyExchangeList.stream()
                .map(currencyExchange -> new CurrencyExchangeResponse(
                        currencyExchange.getId(),
                        currencyExchange.getAddress(),
                        currencyExchange.getName(),
                        currencyExchange.getBusinessHours(),
                        currencyExchange.getDescription(),
                        currencyExchange.getPhone(),
                        currencyExchange.getLatitude(),
                        currencyExchange.getLongitude(),
                        currencyExchange.getExchangeRates().stream()
                                .map(exchangeRate -> new CurrencyExchangeResponse.ExchangeRate(
                                        exchangeRate.getId(),
                                        exchangeRate.getCurrency().name(),
                                        exchangeRate.getExchangeType().name(),
                                        exchangeRate.getAmount(),
                                        exchangeRate.getUpdatedAt()
                                )).toList()
                ))
                .toList();

        // Wrapper로 감싸서 캐시에 저장
        try {
            redisTemplate.opsForValue().set(CACHE_KEY, new CurrencyExchangeResponseWrapper(response));
        } catch (Exception e) {
            log.error("Redis 캐시 저장 실패", e);
        }

        return response;
    }

    // 캐시 수동 갱신이 필요한 경우 호출하는 메서드
    @Scheduled(cron = "0 0 9,12,15,18 * * *")
    public void evictCache() {
        try {
            redisTemplate.delete(CACHE_KEY);
            log.info("Currency exchange cache has been cleared at {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Redis 캐시 삭제 실패", e);
        }
    }
}