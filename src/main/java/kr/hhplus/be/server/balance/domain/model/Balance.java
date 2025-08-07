package kr.hhplus.be.server.balance.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.common.ErrorMessages;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "balance", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class Balance {

    private static final long INITIAL_AMOUNT = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long id;

    private Long userId;

    // null 허용 x
    private long amount;


    @Builder
    private Balance(Long id, Long userId, long amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }

    public static Balance createInitial(Long userId) {
        return Balance.builder()
                .userId(userId)
                .amount(INITIAL_AMOUNT)
                .build();
    }

    public void addAmount(long chargeAmount) {
        if (chargeAmount <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_CHARGE_AMOUNT);
        }
        this.amount += chargeAmount;
    }

    public void deductAmount(long useAmount) {
        if (useAmount <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_USAGE_AMOUNT);
        }
        if (this.amount < useAmount) {
            throw new IllegalArgumentException(ErrorMessages.INSUFFICIENT_BALANCE);
        }
        this.amount -= useAmount;
    }

}
