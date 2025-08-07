package kr.hhplus.be.server.balance.api.dto.response;

import kr.hhplus.be.server.balance.domain.model.Balance;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BalanceResponseDto {
    private long amount;

    public static BalanceResponseDto from(Balance balance) {
        return BalanceResponseDto.builder()
                .amount(balance.getAmount())
                .build();
    }

    public static BalanceResponseDto of(long amount) {
        return BalanceResponseDto.builder()
                .amount(amount)
                .build();
    }
}