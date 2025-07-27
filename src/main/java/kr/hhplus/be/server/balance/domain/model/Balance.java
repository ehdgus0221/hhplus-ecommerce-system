package kr.hhplus.be.server.balance.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.common.exception.ErrorMessages;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "balance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID는 필수입니다.")
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Min(value = 0, message = "잔액은 음수가 될 수 없습니다.")
    @Column(nullable = false)
    private int amount;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public static Balance createInitial(Long userId) {
        return Balance.builder()
                .userId(userId)
                .amount(0)
                .build();
    }

    public void addAmount(int chargeAmount) {
        if (chargeAmount <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_CHARGE_AMOUNT);
        }
        this.amount += chargeAmount;
    }

    public void deductAmount(int useAmount) {
        if (useAmount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
        if (this.amount < useAmount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.amount -= useAmount;
    }

}
