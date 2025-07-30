package kr.hhplus.be.server.balance.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private long amount;

    private String type;

    private String description;

    private LocalDateTime createdAt;

    @Builder
    public BalanceHistory(Long userId, long amount, String type, String description) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.description = description;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public static BalanceHistory charge(Long userId, long amount) {
        return BalanceHistory.builder()
                .userId(userId)
                .amount(amount)
                .type("CHARGE")
                .description("잔액 충전")
                .build();
    }

    public static BalanceHistory use(Long userId, long amount) {
        return BalanceHistory.builder()
                .userId(userId)
                .amount(amount)
                .type("USE")
                .description("잔액 사용")
                .build();
    }

}
