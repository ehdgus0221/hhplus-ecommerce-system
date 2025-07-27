package kr.hhplus.be.server.order.api.dto.response;

import kr.hhplus.be.server.order.domain.model.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderResponseDto {

    private Long orderId;
    private Long productId;
    private Long optionId;
    private Integer stock;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;

    public static OrderResponseDto fromOrder(Order order, Long productId, Long optionId, Integer stock) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .productId(productId)
                .optionId(optionId)
                .stock(stock)
                .status(order.getStatus().name())
                .totalPrice(BigDecimal.valueOf(order.getTotalPrice()))
                .orderDate(order.getOrderDate())
                .build();
    }
}