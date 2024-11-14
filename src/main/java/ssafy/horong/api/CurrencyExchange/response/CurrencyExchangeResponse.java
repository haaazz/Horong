package ssafy.horong.api.CurrencyExchange.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "환전소 및 환율 정보 응답 DTO")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonTypeName("CurrencyExchangeResponse")
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
) implements Serializable {

        @Schema(description = "환율 정보")
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        @JsonTypeName("ExchangeRate")
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
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                LocalDateTime updatedAt
        ) implements Serializable {
                // 유효성 검사를 위한 생성자
                public ExchangeRate {
                        if (id == null) throw new IllegalArgumentException("id cannot be null");
                        if (currency == null) throw new IllegalArgumentException("currency cannot be null");
                        if (exchangeType == null) throw new IllegalArgumentException("exchangeType cannot be null");
                        if (updatedAt == null) throw new IllegalArgumentException("updatedAt cannot be null");
                }
        }

        // 유효성 검사를 위한 생성자
        public CurrencyExchangeResponse {
                if (id == null) throw new IllegalArgumentException("id cannot be null");
                if (address == null) throw new IllegalArgumentException("address cannot be null");
                if (name == null) throw new IllegalArgumentException("name cannot be null");
                if (exchangeRates == null) throw new IllegalArgumentException("exchangeRates cannot be null");
        }

        // List를 위한 Wrapper 클래스
        public static class CurrencyExchangeResponseList implements Serializable {
                private List<CurrencyExchangeResponse> responses;

                public CurrencyExchangeResponseList() {
                }

                public CurrencyExchangeResponseList(List<CurrencyExchangeResponse> responses) {
                        this.responses = responses;
                }

                public List<CurrencyExchangeResponse> getResponses() {
                        return responses;
                }

                public void setResponses(List<CurrencyExchangeResponse> responses) {
                        this.responses = responses;
                }
        }
}