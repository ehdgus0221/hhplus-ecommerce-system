package kr.hhplus.be.server.order.domain.service;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.domain.model.OrderStatus;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderDomainService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public Order createOrder(Long userId, Long productId, Long optionId, Integer stock, String couponId) {

        // 상품 조회
        Product product = productRepository.findByIdOrThrow(productId);

        // 옵션 조회 및 재고 확인
        ProductOption option = product.getOptions().stream()
                .filter(opt -> opt.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다."));

        if (!option.isActive() || option.getStock() < stock) {
            throw new IllegalArgumentException("해당 옵션의 재고가 부족하거나 비활성 상태입니다.");
        }
        option.decreaseStock(stock);

        // 가격 계산
        int unitPrice = product.getBasePrice() + option.getPrice();
        int totalPrice = unitPrice * stock.intValue();

        Order order = Order.create(
                userId,
                couponId != null ? Long.valueOf(couponId) : null,
                totalPrice,
                OrderStatus.ORDERED,
                LocalDateTime.now()
        );

        OrderItem orderItem = OrderItem.create(optionId, stock, unitPrice);
        order.addOrderItem(orderItem); // 책임은 Order가 진다

        orderRepository.save(order);

        return order;
    }

    public void linkPaymentWithOrder(Payment payment, Order order) {
        payment.setOrderId(order.getId());
        paymentRepository.save(payment);
    }
}
