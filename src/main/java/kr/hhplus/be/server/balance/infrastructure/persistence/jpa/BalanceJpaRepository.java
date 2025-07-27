package kr.hhplus.be.server.balance.infrastructure.persistence.jpa;


import kr.hhplus.be.server.balance.domain.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<Balance,Long> {
    Optional<Balance> findByUserId(Long userId);
}
