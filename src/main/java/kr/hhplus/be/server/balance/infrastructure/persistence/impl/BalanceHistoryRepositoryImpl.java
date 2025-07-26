package kr.hhplus.be.server.balance.infrastructure.persistence.impl;

import kr.hhplus.be.server.balance.domain.model.BalanceHistory;
import kr.hhplus.be.server.balance.domain.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.balance.infrastructure.persistence.jpa.BalanceHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BalanceHistoryRepositoryImpl implements BalanceHistoryRepository {

    private final BalanceHistoryJpaRepository balanceHistoryJpaRepository;

    @Override
    public BalanceHistory save(BalanceHistory balanceHistory) {
        return balanceHistoryJpaRepository.save(balanceHistory);
    }

}
