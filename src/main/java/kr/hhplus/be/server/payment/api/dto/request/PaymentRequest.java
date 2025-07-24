package kr.hhplus.be.server.payment.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class PaymentRequest {
    public record Create(
            @Schema(description = "ID", requiredMode = RequiredMode.REQUIRED)
            @NotBlank(message = "ID는 필수입니다.")
            String id,
            @Schema(description = "주문 ID", requiredMode = RequiredMode.REQUIRED)
            @NotBlank(message = "주문 ID는 필수입니다.")
            String orderId
    ) {
    }
}
