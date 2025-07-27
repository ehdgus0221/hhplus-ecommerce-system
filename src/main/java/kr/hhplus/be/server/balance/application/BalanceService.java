package kr.hhplus.be.server.balance.application;

import kr.hhplus.be.server.balance.api.dto.response.BalanceResponseDto;
import kr.hhplus.be.server.balance.domain.model.Balance;
import kr.hhplus.be.server.balance.domain.repository.BalanceRepository;
import kr.hhplus.be.server.balance.domain.service.BalanceDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BalanceService {
    private final BalanceDomainService balanceDomainService;
    private final BalanceRepository balanceRepository;

    public BalanceResponseDto charge(Long userId, int amount) {
        return BalanceResponseDto.from(balanceDomainService.charge(userId, amount));
    }

    @Transactional
    public BalanceResponseDto use(Long userId, int amount) {
        return BalanceResponseDto.from(balanceDomainService.use(userId, amount));
    }

    public BalanceResponseDto get(Long userId) {
        int amount = balanceRepository.findByUserId(userId)
                .map(Balance::getAmount)
                .orElse(0);
        return BalanceResponseDto.of(amount);
    }
}
