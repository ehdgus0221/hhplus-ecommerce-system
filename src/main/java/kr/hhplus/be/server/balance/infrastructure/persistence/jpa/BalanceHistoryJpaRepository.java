package kr.hhplus.be.server.balance.infrastructure.persistence.jpa;

import kr.hhplus.be.server.balance.domain.model.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceHistoryJpaRepository extends JpaRepository<BalanceHistory,Long> {

}
