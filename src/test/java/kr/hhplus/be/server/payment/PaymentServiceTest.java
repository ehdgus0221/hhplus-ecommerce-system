package kr.hhplus.be.server.payment;

import kr.hhplus.be.server.balance.api.dto.response.BalanceResponseDto;
import kr.hhplus.be.server.balance.application.BalanceService;
import kr.hhplus.be.server.payment.application.PaymentService;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private BalanceService balanceService;
    @Mock
    private PaymentDomainService paymentDomainService;

    @Test
    @DisplayName("결제 처리 동작 실패 - 잔액 부족 시 예외 발생")
    void processPayment_balanceFail() {
        doThrow(new IllegalArgumentException("잔액 부족")).when(balanceService).use(1L, 1000);

        assertThatThrownBy(() -> paymentService.processPayment(1L, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잔액 부족");
    }

    @Test
    @DisplayName("결제 처리 동작 성공 - 정상 결제 시 Payment 객체 반환")
    void processPayment_success() {
        Payment payment = mock(Payment.class);

        when(balanceService.use(1L, 1000)).thenReturn(mock(BalanceResponseDto.class));
        when(paymentDomainService.createAndSavePayment(1L, 1000)).thenReturn(payment);

        Payment result = paymentService.processPayment(1L, 1000);

        assertThat(result).isEqualTo(payment);
    }
}
