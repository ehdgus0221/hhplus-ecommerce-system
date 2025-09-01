package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.order.domain.model.OrderEvent;

public interface OrderEventPublisher {
    void publish(OrderEvent.Publish event);
}
