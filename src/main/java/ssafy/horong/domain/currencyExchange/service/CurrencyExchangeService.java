package ssafy.horong.domain.currencyExchange.service;

import ssafy.horong.api.CurrencyExchange.response.CurrencyExchangeResponse;

import java.util.List;

public interface CurrencyExchangeService {
    List<CurrencyExchangeResponse> getCurrencyExchangeList();
}
