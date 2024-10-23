package ssafy.horong.domain.currencyExchange.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CurrencyExchange {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;  // 크롤링한 ID 그대로 사용

    @Column(name = "address", nullable = false, length = 255)
    private String address;  // 환전소 주소

    @Column(name = "name", nullable = false, length = 20)
    private String name;  // 환전소 이름

    @Column(name = "business_hours", nullable = false, length = 100)
    private String businessHours;  // 영업 시간

    @Column(name = "description", nullable = false, length = 200)
    private String description;  // 설명

    @Column(name = "phone", nullable = false, length = 14)
    private String phone;  // 전화번호 (크롤링된 데이터 그대로 사용)

    @OneToMany(mappedBy = "currencyExchange", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExchangeRate> exchangeRates;  // 연관된 환율 정보

    public void updateDetails(String address, String businessHours, String description, String phone) {
        this.address = address;
        this.businessHours = businessHours;
        this.description = description;
        this.phone = phone;
    }

    @PrePersist
    public void prePersist() {
        // 추가적인 기본값 설정이 필요한 경우 작성
    }
}
