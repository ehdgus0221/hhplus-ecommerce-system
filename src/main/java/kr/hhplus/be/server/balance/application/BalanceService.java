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

    private static final long INITIAL_AMOUNT = 0L;

    private final BalanceDomainService balanceDomainService;
    private final BalanceRepository balanceRepository;

    public BalanceResponseDto charge(Long userId, long amount) {
        return BalanceResponseDto.from(balanceDomainService.charge(userId, amount));
    }

    @Transactional
    public BalanceResponseDto use(Long userId, long amount) {
        return BalanceResponseDto.from(balanceDomainService.use(userId, amount));
    }

    public BalanceResponseDto get(Long userId) {
        long amount = balanceRepository.findByUserId(userId)
                .map(Balance::getAmount)
                .orElse(INITIAL_AMOUNT);
        return BalanceResponseDto.of(amount);
    }
}
