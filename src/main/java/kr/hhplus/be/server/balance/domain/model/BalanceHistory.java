package kr.hhplus.be.server.balance.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "balance_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BalanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String type;  // 예: CHARGE, USE 등

    @Column(length = 255)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public static BalanceHistory charge(Long userId, int amount) {
        return BalanceHistory.builder()
                .userId(userId)
                .amount(amount)
                .type("CHARGE")
                .description("잔액 충전")
                .build();
    }

    public static BalanceHistory use(Long userId, int amount) {
        return BalanceHistory.builder()
                .userId(userId)
                .amount(amount)
                .type("USE")
                .description("잔액 사용")
                .build();
    }

    public static BalanceHistory deduct(Long userId, int amount) {
        return BalanceHistory.builder()
                .userId(userId)
                .amount(amount)
                .type("DEDUCT")
                .description("잔액 취소?")
                .build();
    }
}
