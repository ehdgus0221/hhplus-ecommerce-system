package kr.hhplus.be.server.balance.domain.repository;

import kr.hhplus.be.server.balance.domain.model.Balance;

import java.util.Optional;

public interface BalanceRepository{
    Optional<Balance> findByUserId(Long userId);
    Balance save(Balance balance);
}
