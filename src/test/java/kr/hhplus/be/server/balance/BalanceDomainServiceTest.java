package kr.hhplus.be.server.balance;

import kr.hhplus.be.server.balance.domain.model.Balance;
import kr.hhplus.be.server.balance.domain.repository.BalanceRepository;
import kr.hhplus.be.server.balance.domain.service.BalanceDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceDomainServiceTest {

    @InjectMocks
    private BalanceDomainService balanceDomainService;

    @Mock
    private BalanceRepository balanceRepository;

    @Test
    @DisplayName("잔액 사용 실패 - 잔액 정보가 존재하지 않는 경우 예외 발생")
    void use_fail_notFound() {
        when(balanceRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceDomainService.use(1L, 500))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액 정보가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("잔액 충전 성공 - 신규 사용자 잔액 생성 및 충전")
    void charge_success() {
        when(balanceRepository.findByUserId(1L)).thenReturn(Optional.empty());

        Balance result = balanceDomainService.charge(1L, 1000);

        assertThat(result.getAmount()).isEqualTo(1000);
    }

}
