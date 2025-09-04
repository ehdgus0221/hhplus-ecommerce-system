package kr.hhplus.be.server.order.domain.repository;

import kr.hhplus.be.server.order.domain.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent,Long> {
    List<OutboxEvent> findByPublishedFalse();
}
