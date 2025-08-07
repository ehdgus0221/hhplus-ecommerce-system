package kr.hhplus.be.server.payment.domain.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.ErrorMessages;
import kr.hhplus.be.server.common.InvalidAmountException;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private long paidAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private LocalDateTime createdAt;


    @Builder
    private Payment(Long id, Long orderId, long paidAmount, PaymentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.paidAmount = paidAmount;
        this.status = status;
    }

    public static Payment create(Long orderId, long amount) {
        if (amount <= 0) throw new IllegalStateException(ErrorMessages.PAYMENT_PRICE_INVALID);
        return Payment.builder()
                .orderId(orderId)
                .paidAmount(amount)
                .status(PaymentStatus.PENDING)
                .build();
    }

    public void pay() {
        if (!status.equals(PaymentStatus.PENDING)) {
            throw new IllegalStateException("결제 가능 상태가 아닙니다.");
        }
        this.status = PaymentStatus.COMPLETED;
    }
}
