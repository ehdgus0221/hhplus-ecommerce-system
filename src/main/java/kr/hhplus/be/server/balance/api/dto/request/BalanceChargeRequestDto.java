package kr.hhplus.be.server.balance.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BalanceChargeRequestDto {

    @NotNull(message = "충전 금액은 필수입니다.")
    @Min(value = 1, message = "충전 금액은 1원 이상이어야 합니다.")
    private long amount;
}
