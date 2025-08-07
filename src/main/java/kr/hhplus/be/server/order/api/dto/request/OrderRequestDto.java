package kr.hhplus.be.server.order.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.order.domain.model.OrderItem;

import java.util.List;

public class OrderRequestDto {
    public record Create(
            String productId,
            String productOptionId,
            Integer stock,
            String userId,
            String couponId
    ) {
    }
}
