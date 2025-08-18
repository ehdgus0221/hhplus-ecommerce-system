package kr.hhplus.be.server.payment;

import kr.hhplus.be.server.balance.domain.model.Balance;
import kr.hhplus.be.server.balance.domain.repository.BalanceRepository;
import kr.hhplus.be.server.payment.application.PaymentService;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentStatus;
import kr.hhplus.be.server.payment.infrastructure.persistence.jpa.PaymentJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
public class PaymentIntegrationTest {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Test
    @DisplayName("정상 결제 시 Payment가 저장되고 상태가 COMPLETED가 된다.")
    void processPayment_successfulPayment() {
        // given
        Long userId = 1L;
        long initialBalance = 10000L;
        long paymentAmount = 5000L;

        Balance balance = Balance.createInitial(userId);
        balance.addAmount(initialBalance);

        balanceRepository.save(balance);

        // when
        Payment payment = paymentService.processPayment(userId, paymentAmount);

        // then
        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getPaidAmount()).isEqualTo(paymentAmount);

        Balance updatedBalance = balanceRepository.findByUserId(userId).orElseThrow();
        assertThat(updatedBalance.getAmount()).isEqualTo(initialBalance - paymentAmount);
    }

    @Test
    @DisplayName("결제 금액이 0 이하이면 예외가 발생한다.")
    void processPayment_invalidAmount_throwsException() {
        // given
        Long userId = 1L;
        long initialBalance = 10000L;
        long invalidAmount = 0L;

        Balance balance = Balance.createInitial(userId);
        balance.addAmount(initialBalance);

        balanceRepository.save(balance);

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(userId, invalidAmount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 금액");
    }

    @Test
    @DisplayName("잔액이 부족하면 예외가 발생한다.")
    void processPayment_insufficientBalance_throwsException() {
        // given
        Long userId = 1L;
        long initialBalance = 3000L;
        long paymentAmount = 5000L;

        Balance balance = Balance.createInitial(userId);
        balance.addAmount(initialBalance);

        balanceRepository.save(balance);

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(userId, paymentAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잔액이 부족합니다.");
    }
}
