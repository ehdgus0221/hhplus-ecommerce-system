package kr.hhplus.be.server.balance;

import kr.hhplus.be.server.balance.api.dto.response.BalanceResponseDto;
import kr.hhplus.be.server.balance.application.BalanceService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private BalanceDomainService balanceDomainService;

    @Mock
    private BalanceRepository balanceRepository;


    @Test
    @DisplayName("잔액 충전 실패 - 충전 금액이 0 이하일 때 예외 발생")
    void charge_fail_invalidAmount() {
        // balanceDomainService.charge 호출 시 IllegalArgumentException 예외를 던지도록 설정
        when(balanceDomainService.charge(1L, 0))
                .thenThrow(new IllegalArgumentException("충전 금액은 1 이상이어야 합니다."));

        // 실제 서비스 호출 시 예외 발생 여부 검증
        assertThatThrownBy(() -> balanceService.charge(1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 금액은 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("잔액 조회 시 잔액이 없으면 0 반환")
    void get_balanceNotExist() {
        when(balanceRepository.findByUserId(1L)).thenReturn(Optional.empty());

        BalanceResponseDto dto = balanceService.get(1L);

        assertThat(dto.getAmount()).isEqualTo(0);
    }

    @Test
    @DisplayName("잔액 충전 성공 - 도메인 서비스 호출 후 DTO 반환")
    void charge_success() {
        Balance balance = mock(Balance.class);
        when(balanceDomainService.charge(1L, 1000)).thenReturn(balance);

        BalanceResponseDto dto = balanceService.charge(1L, 1000);

        assertThat(dto).isNotNull();
    }

}
