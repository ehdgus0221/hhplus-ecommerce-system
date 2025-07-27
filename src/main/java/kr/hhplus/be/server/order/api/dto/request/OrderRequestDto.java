package kr.hhplus.be.server.order.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderRequestDto {
    public record Create(
            @Schema(description = "상품 ID", requiredMode = RequiredMode.REQUIRED)
            @NotBlank(message = "상품 ID는 필수입니다.")
            String productId,
            @Schema(description = "상품옵션 ID", requiredMode = RequiredMode.REQUIRED)
            @NotBlank(message = "상품옵션 ID는 필수입니다.")
            String productOptionId,
            @Schema(description = "상품 수량", requiredMode = RequiredMode.REQUIRED)
            @NotNull(message = "수량은 필수입니다.")
            @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
            Integer stock,
            @Schema(description = "사용자 ID", requiredMode = RequiredMode.NOT_REQUIRED)
            String userId,
            @Schema(description = "쿠폰 ID", requiredMode = RequiredMode.NOT_REQUIRED)
            String couponId
    ) {
    }
}
