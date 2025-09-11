package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.order.domain.model.OutboxEvent;
import kr.hhplus.be.server.order.domain.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.dlq}")
    private String dlqTopic;

    @TransactionalEventListener
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = repository.findByPublishedFalse();

        for (OutboxEvent event : pendingEvents) {
            try {
                // send()가 CompletableFuture 반환
                CompletableFuture<SendResult<String, Object>> future =
                        kafkaTemplate.send(
                                event.getEventType(),
                                event.getAggregateId().toString(),
                                DataSerializer.deserialize(event.getPayload(), Object.class)
                        );

                future.thenAccept(result -> {
                    event.markAsPublished();
                    repository.save(event);
                    log.info("Kafka 전송 성공: eventId={}, topic={}", event.getId(), event.getEventType());
                }).exceptionally(ex -> {
                    log.error("Kafka 전송 실패: eventId={}, topic={}, error={}", event.getId(),
                            event.getEventType(), ex.getMessage(), ex);
                    sendToDLQ(event, ex);
                    return null;
                });

            } catch (Exception ex) {
                log.error("Outbox 이벤트 처리 중 예외 발생: eventId={}, error={}", event.getId(), ex.getMessage(), ex);
                sendToDLQ(event, ex);
            }
        }
    }

    private void sendToDLQ(OutboxEvent event, Throwable ex) {
        // Kafka DLQ 발행
        try {
            kafkaTemplate.send(dlqTopic, event.getAggregateId().toString(), event)
                    .whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            log.error("DLQ Kafka 전송 실패: eventId={}, error={}", event.getId(), throwable.getMessage(), throwable);
                        } else {
                            log.info("DLQ Kafka 전송 완료: eventId={}", event.getId());
                        }
                    });
        } catch (Exception kafkaEx) {
            log.error("DLQ Kafka 전송 예외: eventId={}, error={}", event.getId(), kafkaEx.getMessage(), kafkaEx);
        }

        // DB에 DLQ 기록
        try {
            OutboxEvent dlqEvent = OutboxEvent.builder()
                    .eventType("DLQ_" + event.getEventType())
                    .aggregateId(event.getAggregateId())
                    .payload(event.getPayload())
                    .createdAt(LocalDateTime.now())
                    .published(false)
                    .build();
            repository.save(dlqEvent);
            log.info("DLQ 이벤트 DB 기록 완료: eventId={}", event.getId());
        } catch (Exception dbEx) {
            log.error("DLQ 이벤트 DB 기록 실패: eventId={}, error={}", event.getId(), dbEx.getMessage(), dbEx);
        }
    }
}

