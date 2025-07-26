package kr.hhplus.be.server.balance.domain.service;

import kr.hhplus.be.server.balance.domain.model.Balance;
import kr.hhplus.be.server.balance.domain.model.BalanceHistory;
import kr.hhplus.be.server.balance.domain.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.balance.domain.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceDomainService {

    private final BalanceRepository balanceRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    @Transactional
    public Balance charge(Long userId, int amount) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElse(Balance.createInitial(userId));
        balance.addAmount(amount);

        balanceRepository.save(balance);
        balanceHistoryRepository.save(BalanceHistory.charge(userId, amount));

        return balance;
    }

    @Transactional
    public Balance use(Long userId, int amount) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("잔액 정보가 존재하지 않습니다."));
        balance.deductAmount(amount);

        balanceRepository.save(balance);
        balanceHistoryRepository.save(BalanceHistory.use(userId, amount));

        return balance;
    }
}
