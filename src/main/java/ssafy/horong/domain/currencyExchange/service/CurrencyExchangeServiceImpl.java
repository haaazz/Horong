package ssafy.horong.domain.currencyExchange.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssafy.horong.api.CurrencyExchange.response.CurrencyExchangeResponse;
import ssafy.horong.domain.currencyExchange.entity.CurrencyExchange;
import ssafy.horong.domain.currencyExchange.repository.CurrencyExchangeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private final CurrencyExchangeRepository currencyExchangeRepository;

    @Override
    public List<CurrencyExchangeResponse> getCurrencyExchangeList() {
        List<CurrencyExchange> currencyExchangeList = currencyExchangeRepository.findAll();

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
                                )).toList()
                ))
                .toList();
    }
}
