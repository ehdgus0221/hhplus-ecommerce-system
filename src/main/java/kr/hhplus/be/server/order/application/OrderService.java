package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.service.OrderDomainService;
import kr.hhplus.be.server.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDomainService orderDomainService;

    public Order createOrder(Long userId, Long productId, Long optionId, Integer stock, String couponId) {
        return orderDomainService.createOrder(userId, productId, optionId, stock, couponId);
    }

    public void linkPaymentWithOrder(Payment payment, Order order) {
        orderDomainService.linkPaymentWithOrder(payment, order);
    }

    public void restoreStock(Order order) {
        orderDomainService.restoreStock(order);
    }

}
