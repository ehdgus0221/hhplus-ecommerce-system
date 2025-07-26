package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.balance.application.BalanceService;
import kr.hhplus.be.server.order.api.dto.request.OrderRequestDto;
import kr.hhplus.be.server.order.api.dto.response.OrderResponseDto;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.payment.application.PaymentService;
import kr.hhplus.be.server.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final BalanceService balanceService;

    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto.Create request) {
        Long productId = Long.valueOf(request.productId());
        Long optionId = Long.valueOf(request.productOptionId());
        Integer stock = request.stock();
        Long userId = request.userId() != null ? Long.valueOf(request.userId()) : 0L;
        String couponId = request.couponId();

        // 1. 주문 생성 및 재고 차감
        Order order = orderService.createOrder(userId, productId, optionId, stock, couponId);

        // 2. 잔액 차감 (잔액 부족 시 예외 발생)
        balanceService.use(userId, order.getTotalPrice());

        // 3. 결제 처리
        Payment payment = paymentService.processPayment(userId, order.getTotalPrice());

        // 4. 결제와 주문 연결
        orderService.linkPaymentWithOrder(payment, order);

        // 5. 응답 반환
        return OrderResponseDto.fromOrder(order, productId, optionId, stock);
    }
}
