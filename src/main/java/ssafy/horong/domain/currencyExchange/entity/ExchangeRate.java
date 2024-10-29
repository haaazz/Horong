package ssafy.horong.domain.currencyExchange.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;  // 엔화, 위안화, 달러 등 지원할 화폐 종류

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeType exchangeType;  // 구매 또는 판매 여부

    @Column(nullable = false)
    private double amount;  // 환율 금액

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정된 시간

    @ManyToOne
    @JoinColumn(name = "currency_exchange_id", nullable = false)
    private CurrencyExchange currencyExchange;  // 환전소와의 관계

    // 엔티티가 처음 저장될 때 실행
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.updatedAt = now;   // 처음 저장할 때 업데이트 시간도 동일하게 설정
    }

    // 엔티티가 업데이트될 때 실행
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();  // 업데이트될 때마다 업데이트 시간 갱신
    }

    // 화폐 종류 (Enum 타입)
    public enum Currency {
        JPY, // 일본 엔화
        CNY, // 중국 위안화
        USD  // 미국 달러
    }

    // 거래 유형 (Enum 타입)
    public enum ExchangeType {
        BUY,  // 구매
        SELL  // 판매
    }
}
