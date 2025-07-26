package kr.hhplus.be.server.order;

import kr.hhplus.be.server.order.application.OrderService;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.service.OrderDomainService;
import kr.hhplus.be.server.payment.domain.model.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderDomainService orderDomainService;

    @Test
    @DisplayName("주문 생성 실패 - OrderDomainService에서 예외 발생 시 예외가 전파된다")
    void createOrder_fail() {
        when(orderDomainService.createOrder(1L, 2L, 3L, 1, null))
                .thenThrow(new IllegalArgumentException("상품 옵션 재고 부족"));

        assertThatThrownBy(() -> orderService.createOrder(1L, 2L, 3L, 1, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 옵션 재고 부족");
    }

    @Test
    @DisplayName("결제 주문 연동 실패 - OrderDomainService에서 예외 발생 시 예외가 전파된다")
    void linkPaymentWithOrder_fail() {
        Payment payment = mock(Payment.class);
        Order order = mock(Order.class);

        doThrow(new RuntimeException("결제 연동 실패"))
                .when(orderDomainService).linkPaymentWithOrder(payment, order);

        assertThatThrownBy(() -> orderService.linkPaymentWithOrder(payment, order))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("결제 연동 실패");
    }

    @Test
    @DisplayName("주문 생성 성공 - OrderDomainService의 결과를 그대로 반환한다")
    void createOrder_success() {
        Order mockOrder = mock(Order.class);
        when(orderDomainService.createOrder(1L, 2L, 3L, 1, null)).thenReturn(mockOrder);

        Order order = orderService.createOrder(1L, 2L, 3L, 1, null);

        assertThat(order).isEqualTo(mockOrder);
    }

    @Test
    @DisplayName("결제 주문 연동 성공 - 예외 없이 도메인 서비스 호출 검증")
    void linkPaymentWithOrder_success() {
        Payment payment = mock(Payment.class);
        Order order = mock(Order.class);

        assertDoesNotThrow(() -> orderService.linkPaymentWithOrder(payment, order));
        verify(orderDomainService).linkPaymentWithOrder(payment, order);
    }
}
