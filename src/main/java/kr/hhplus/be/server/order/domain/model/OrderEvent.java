package kr.hhplus.be.server.order.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class OrderEvent {

    @Getter
    public static class Publish {
        private final Long orderId;
        private final Long productId;
        private final Long optionId;
        private final long stock;
        private final LocalDateTime orderDate;

        @Builder
        private Publish(Long orderId,
                     Long productId,
                     Long optionId,
                     long stock,
                     LocalDateTime orderDate) {
            this.orderId = orderId;
            this.productId = productId;
            this.optionId = optionId;
            this.stock = stock;
            this.orderDate = orderDate;
        }

        public static Publish of(Order order, Long productId, Long optionId, long stock) {
            return Publish.builder()
                    .orderId(order.getId())
                    .productId(productId)
                    .optionId(optionId)
                    .stock(stock)
                    .orderDate(order.getOrderDate())
                    .build();
        }
    }
}
