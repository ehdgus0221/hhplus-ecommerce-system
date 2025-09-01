package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.order.domain.model.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final StringRedisTemplate redisTemplate;
    private static final String ORDER_CHANNEL = "order-events";

    /**
     * 트랜잭션 커밋 후 비동기적으로 Redis 채널에 이벤트 발행
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPublishedEvent(OrderEvent.Publish event) {
        String message = serialize(event);
        redisTemplate.convertAndSend(ORDER_CHANNEL, message);
        log.info("Redis로 주문 이벤트 발행: {}", message);
    }

    private String serialize(OrderEvent.Publish event) {
        // 간단히 JSON 형식으로 변환
        return String.format(
                "{\"orderId\":%d,\"productId\":%d,\"optionId\":%d,\"stock\":%d,\"orderDate\":\"%s\"}",
                event.getOrderId(),
                event.getProductId(),
                event.getOptionId(),
                event.getStock(),
                event.getOrderDate()
        );
    }
}
