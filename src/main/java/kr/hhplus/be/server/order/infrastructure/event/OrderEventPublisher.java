package kr.hhplus.be.server.order.infrastructure.event;

import kr.hhplus.be.server.order.domain.model.OrderEvent;

public interface OrderEventPublisher {
    void created(OrderEvent.Created event);
    void completed(OrderEvent.Completed event);
    void failed(OrderEvent.Failed event);
}
