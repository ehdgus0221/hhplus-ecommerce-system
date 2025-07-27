package kr.hhplus.be.server.order.infrastructure.persistence.impl;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.order.infrastructure.persistence.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    };
}
