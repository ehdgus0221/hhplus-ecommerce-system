package kr.hhplus.be.server.order.domain.service;

import kr.hhplus.be.server.order.application.OrderEventPublisher;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.model.OrderEvent;
import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.domain.model.OrderStatus;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDomainService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductOptionRepository productOptionRepository;
    private final OrderEventPublisher orderEventPublisher;

    public Order createOrder(Long userId, Long productId, Long optionId, long quantity, long couponId) {

        Product product = productRepository.findByIdOrThrow(productId);

        // 옵션 조회 및 재고 확인
        // 비관적 락 적용
        ProductOption option = productOptionRepository.findWithLockById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));

        if (option.isOutOfStock() || option.isStockInsufficient(quantity)) {
            throw new IllegalArgumentException("해당 옵션의 재고가 부족하거나 비활성 상태입니다.");
        }
        option.decreaseStock(quantity);

        Order order = Order.create(
                userId,
                couponId ,
                product.getBasePrice(),
                OrderStatus.ORDERED,
                LocalDateTime.now()
        );

        OrderItem orderItem = OrderItem.create(optionId, product.getName(), quantity, product.getBasePrice());
        order.addOrderItem(orderItem); // 책임은 Order가 진다

        orderRepository.save(order);

        return order;
    }

    public void linkPaymentWithOrder(Payment payment, Order order) {
        payment.setOrderId(order.getId());
        paymentRepository.save(payment);
    }

    public void restoreStock(Order order) {
        order.getOrderItems().forEach(orderItem -> {
            Long optionId = orderItem.getProductOptionId();
            long quantity = orderItem.getStock();

            // 상품 옵션 조회 (상품 전체를 다시 조회해도 되고, 옵션만 조회해도 됨)
            // 아래는 옵션만 단독 조회할 수 있는 메서드가 있다고 가정
            ProductOption option = findProductOptionById(optionId);

            option.increaseStock(quantity);

            // 영속성 반영 필요 시 저장
            productRepository.save(option.getProduct());
        });
    }

    private ProductOption findProductOptionById(Long optionId) {
        // 상품 전체를 조회해서 옵션 찾기

        return productOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));
    }

    public void publish(Order order, Long productId, Long optionId, long stock) {
        orderEventPublisher.publish(OrderEvent.Publish.of(order, productId, optionId, stock));
    }


}
