package ssafy.horong.api.CurrencyExchange;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.api.CurrencyExchange.response.CurrencyExchangeResponse;
import ssafy.horong.domain.currencyExchange.service.CurrencyExchangeService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/currency")
@Tag(name = "CurrencyExchange", description = "환율정보")
public class CurrencyExchangeController {

    private final CurrencyExchangeService currencyExchangeService;

    @Operation(summary = "환율정보 조회", description = "환율정보를 조회하는 API입니다.")
    @GetMapping
    public CommonResponse<List<CurrencyExchangeResponse>> getCurrencyExchange() {
        List<CurrencyExchangeResponse> response = currencyExchangeService.getCurrencyExchangeList();
        return CommonResponse.ok(response);
    }
}
