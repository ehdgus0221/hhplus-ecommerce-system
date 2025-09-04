package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.model.OrderEvent;
import kr.hhplus.be.server.order.infrastructure.event.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventService {

    private final OrderEventPublisher orderEventPublisher;

    /**
     * 주문 완료 이벤트 발행
     */
    public void publishOrderCompleted(Order order) {
        // OrderEvent.Completed 객체 생성
        OrderEvent.Completed completedEvent = OrderEvent.Completed.of(order);

        // OutboxEventPublisher를 통해 DB 저장
        orderEventPublisher.completed(completedEvent);
    }

    /**
     * 주문 실패 이벤트 발행
     */
    public void publishOrderFailed(Order order, String reason) {
        //  OrderEvent.Failed 객체 생성
        OrderEvent.Failed failedEvent = OrderEvent.Failed.of(order, reason);

        //  OutboxEventPublisher를 통해 DB 저장
        orderEventPublisher.failed(failedEvent);
    }
}
