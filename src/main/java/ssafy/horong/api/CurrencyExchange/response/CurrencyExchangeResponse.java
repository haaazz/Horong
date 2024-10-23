package ssafy.horong.api.CurrencyExchange.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "환전소 및 환율 정보 응답 DTO")
public record CurrencyExchangeResponse(
        @Schema(description = "환전소 id", example = "1")
        Long id,
        @Schema(description = "환전소 주소", example = "서울특별시 강남구 테헤란로 123")
        String address,
        @Schema(description = "환전소 이름", example = "환전소 이름")
        String name,
        @Schema(description = "영업 시간", example = "09:00 - 18:00")
        String businessHours,
        @Schema(description = "환전소 설명", example = "안전하고 신뢰할 수 있는 환전소입니다.")
        String description,
        @Schema(description = "환전소 전화번호", example = "010-1234-5678")
        String phone,
        @Schema(description = "환전소 위도", example = "37.5665")
        Double latitude,
        @Schema(description = "환전소 경도", example = "126.9780")
        Double longitude,
        @Schema(description = "환율 정보 리스트")
        List<ExchangeRate> exchangeRates
) {
    @Schema(description = "환율 정보")
    public record ExchangeRate(
            @Schema(description = "환율 id", example = "1")
            Long id,
            @Schema(description = "화폐 종류", example = "USD")
            String currency,
            @Schema(description = "거래 유형", example = "BUY")
            String exchangeType,
            @Schema(description = "환율 금액", example = "1100.50")
            double amount,
            @Schema(description = "업데이트 시간", example = "2023-01-01T12:00:00")
            LocalDateTime updatedAt
    ) {}
}
