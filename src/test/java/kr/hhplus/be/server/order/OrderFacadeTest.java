package kr.hhplus.be.server.order;

import kr.hhplus.be.server.balance.api.dto.response.BalanceResponseDto;
import kr.hhplus.be.server.balance.application.BalanceService;
import kr.hhplus.be.server.order.api.dto.request.OrderRequestDto;
import kr.hhplus.be.server.order.api.dto.response.OrderResponseDto;
import kr.hhplus.be.server.order.application.OrderFacade;
import kr.hhplus.be.server.order.application.OrderService;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.model.OrderStatus;
import kr.hhplus.be.server.payment.application.PaymentService;
import kr.hhplus.be.server.payment.domain.model.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @InjectMocks
    private OrderFacade orderFacade;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private BalanceService balanceService;

    @Test
    @DisplayName("주문 처리 실패 - 잔액 부족으로 인한 예외 발생")
    void placeOrder_fail_dueToInsufficientBalance() {
        // given
        OrderRequestDto.Create request = new OrderRequestDto.Create(2L, 3L, 2, 1L, 1L);

        var order = mock(kr.hhplus.be.server.order.domain.model.Order.class);
        when(order.getTotalPrice()).thenReturn(3000L);
        when(orderService.createOrder(anyLong(), anyLong(), anyLong(), anyInt(), any()))
                .thenReturn(order);

        doThrow(new IllegalArgumentException("잔액 부족")).when(balanceService).use(anyLong(), anyInt());

        // when & then
        assertThatThrownBy(() -> orderFacade.placeOrder(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잔액 부족");

        verify(orderService).createOrder(1L, 2L, 3L, 2, null);
        verify(balanceService).use(1L, 3000);
        verify(paymentService, never()).processPayment(anyLong(), anyInt());
        verify(orderService, never()).linkPaymentWithOrder(any(), any());
    }

    @Test
    @DisplayName("주문 처리 실패 - 상품이 존재하지 않아 예외 발생")
    void placeOrder_failDueToOrderCreation() {
        // given
        OrderRequestDto.Create request = new OrderRequestDto.Create(2L, 3L, 2, 1L, 1L);

        when(orderService.createOrder(anyLong(), anyLong(), anyLong(), anyInt(), any()))
                .thenThrow(new IllegalArgumentException("상품이 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> orderFacade.placeOrder(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품이 존재하지 않습니다.");

        verify(orderService).createOrder(1L, 2L, 3L, 2, null);
        verify(balanceService, never()).use(anyLong(), anyInt());
        verify(paymentService, never()).processPayment(anyLong(), anyInt());
        verify(orderService, never()).linkPaymentWithOrder(any(), any());
    }

    @Test
    @DisplayName("주문 처리 성공 - 정상 흐름")
    void placeOrder_success() {
        // given
        OrderRequestDto.Create request = new OrderRequestDto.Create(2L, 3L, 2, 1L, 1L);

        var order = mock(kr.hhplus.be.server.order.domain.model.Order.class);
        when(order.getTotalPrice()).thenReturn(3000L);
        when(order.getStatus()).thenReturn(OrderStatus.ORDERED); // 상태 세팅
        when(orderService.createOrder(anyLong(), anyLong(), anyLong(), anyInt(), any()))
                .thenReturn(order);

        var payment = mock(Payment.class);
        when(paymentService.processPayment(anyLong(), anyInt())).thenReturn(payment);
        when(balanceService.use(anyLong(), anyInt())).thenReturn(mock(BalanceResponseDto.class));
        doNothing().when(orderService).linkPaymentWithOrder(any(Payment.class), any());

        // when
        OrderResponseDto response = orderFacade.placeOrder(request);

        // then
        assertThat(response).isNotNull();

        verify(orderService).createOrder(1L, 2L, 3L, 2, null);  // request.userId() = "1"이므로 userId=1L
        verify(balanceService).use(1L, 3000);
        verify(paymentService).processPayment(1L, 3000);
        verify(orderService).linkPaymentWithOrder(payment, order);
    }

    @Test
    @DisplayName("결제 실패 시 재고 복구 호출 및 예외 전파")
    void placeOrder_paymentFail_shouldRestoreStock() {
        // given
        OrderRequestDto.Create request = new OrderRequestDto.Create(2L, 3L, 2, 1L, 1L);

        Order order = mock(Order.class);
        when(order.getTotalPrice()).thenReturn(3000L);

        when(orderService.createOrder(anyLong(), anyLong(), anyLong(), anyInt(), any()))
                .thenReturn(order);

        when(balanceService.use(anyLong(), anyInt())).thenReturn(null);

        when(paymentService.processPayment(anyLong(), anyInt()))
                .thenThrow(new RuntimeException("결제 실패"));

        doNothing().when(orderService).restoreStock(order);

        // when & then
        assertThatThrownBy(() -> orderFacade.placeOrder(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("결제 실패");

        verify(orderService).createOrder(1L, 2L, 3L, 2, null);
        verify(balanceService).use(1L, 3000);
        verify(paymentService).processPayment(1L, 3000);
        verify(orderService).restoreStock(order); // 복구 호출 확인
        verify(orderService, never()).linkPaymentWithOrder(any(), any());
    }
}