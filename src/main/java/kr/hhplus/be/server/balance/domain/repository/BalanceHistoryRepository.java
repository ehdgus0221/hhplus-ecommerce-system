package kr.hhplus.be.server.balance.domain.repository;


import kr.hhplus.be.server.balance.domain.model.BalanceHistory;

public interface BalanceHistoryRepository{
    BalanceHistory save(BalanceHistory balanceHistory);
}
