package kr.hhplus.be.server.order.infrastructure.event;

import kr.hhplus.be.server.order.application.OrderEventPublisher;
import kr.hhplus.be.server.order.domain.model.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSpringEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(OrderEvent.Publish orderEvent) {
        publisher.publishEvent(orderEvent);
    }

}
