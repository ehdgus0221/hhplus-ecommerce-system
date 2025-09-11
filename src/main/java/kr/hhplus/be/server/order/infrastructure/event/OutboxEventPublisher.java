package kr.hhplus.be.server.order.infrastructure.event;


import kr.hhplus.be.server.order.application.DataSerializer;
import kr.hhplus.be.server.order.domain.model.EventType;
import kr.hhplus.be.server.order.domain.model.OutboxEvent;
import kr.hhplus.be.server.order.domain.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository repository;

    @Transactional
    public void publishEvent(EventType eventType, Long aggregateId, Object eventPayload) {
        OutboxEvent event = OutboxEvent.of(eventType.name(), aggregateId, DataSerializer.serialize(eventPayload));
        repository.save(event);
    }

}
