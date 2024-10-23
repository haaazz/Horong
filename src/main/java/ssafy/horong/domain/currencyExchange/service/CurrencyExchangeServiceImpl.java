package ssafy.horong.domain.currencyExchange.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssafy.horong.api.CurrencyExchange.response.CurrencyExchangeResponse;
import ssafy.horong.domain.currencyExchange.entity.CurrencyExchange;
import ssafy.horong.domain.currencyExchange.repository.CurrencyExchangeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private final CurrencyExchangeRepository currencyExchangeRepository;

    @Override
    public List<CurrencyExchangeResponse> getCurrencyExchangeList() {
        // 데이터베이스에서 모든 환전소 리스트를 가져옴
        List<CurrencyExchange> currencyExchangeList = currencyExchangeRepository.findAll();

        // 환전소 리스트를 CurrencyExchangeResponse로 변환하여 반환
        return currencyExchangeList.stream()
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
                                )).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}
