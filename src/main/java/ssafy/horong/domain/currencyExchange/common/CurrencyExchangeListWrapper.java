package ssafy.horong.domain.currencyExchange.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.horong.api.CurrencyExchange.response.CurrencyExchangeResponse;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyExchangeListWrapper implements Serializable {
    private List<CurrencyExchangeResponse> exchanges;
}
