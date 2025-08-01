package kr.hhplus.be.server.balance.infrastructure.persistence.impl;

import kr.hhplus.be.server.balance.domain.model.Balance;
import kr.hhplus.be.server.balance.domain.repository.BalanceRepository;
import kr.hhplus.be.server.balance.infrastructure.persistence.jpa.BalanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class BalanceRepositoryImpl implements BalanceRepository {

    private final BalanceJpaRepository balanceJpaRepository;

    @Override
    public Optional<Balance> findByUserId(Long userId){
        return balanceJpaRepository.findByUserId(userId);
    }

    @Override
    public Balance save(Balance balance){
        return balanceJpaRepository.save(balance);
    }
}
