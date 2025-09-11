package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.order.domain.model.EventType;
import kr.hhplus.be.server.order.domain.model.OrderEvent;
import kr.hhplus.be.server.order.infrastructure.event.OrderEventPublisher;
import kr.hhplus.be.server.order.infrastructure.event.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisherImpl implements OrderEventPublisher {

    private final OutboxEventPublisher outboxEventPublisher; // 실제 DB outbox 테이블에 저장 후 배치 발행

    //TODO 추가 기능 구현 시 사용하기
    @Override
    public void created(OrderEvent.Created event) {
        log.info("Outbox에 ORDER_CREATED 이벤트 저장: orderId={}", event.getOrderId());
        outboxEventPublisher.publishEvent(EventType.ORDER_CREATED, event.getOrderId(), event);
    }

    @Override
    public void completed(OrderEvent.Completed event) {
        log.info("Outbox에 ORDER_COMPLETED 이벤트 저장: orderId={}", event.getOrderId());
        outboxEventPublisher.publishEvent(EventType.ORDER_COMPLETED, event.getOrderId(), event);
    }

    @Override
    public void failed(OrderEvent.Failed event) {
        log.info("Outbox에 ORDER_COMPLETE_FAILED 이벤트 저장: orderId={}, reason={}", event.getOrderId(), event.getReason());
        outboxEventPublisher.publishEvent(EventType.ORDER_COMPLETE_FAILED, event.getOrderId(), event);
    }

}
