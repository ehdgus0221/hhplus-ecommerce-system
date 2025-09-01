package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.service.OrderDomainService;
import kr.hhplus.be.server.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDomainService orderDomainService;

    @Transactional
    public Order createOrder(Long userId, Long productId, Long optionId, Integer stock, Long couponId) {
        return orderDomainService.createOrder(userId, productId, optionId, stock, couponId);
    }

    @Transactional
    public void linkPaymentWithOrder(Payment payment, Order order) {
        orderDomainService.linkPaymentWithOrder(payment, order);
    }

    @Transactional
    public void restoreStock(Order order) {
        orderDomainService.restoreStock(order);
    }

    @Transactional
    public void publish(Order order, Long productId, Long optionId, long stock) {
        orderDomainService.publish(order, productId, optionId, stock);
    }

}
