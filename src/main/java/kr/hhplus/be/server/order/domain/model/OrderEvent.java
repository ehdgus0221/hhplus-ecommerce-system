package kr.hhplus.be.server.order.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class OrderEvent {

    @Getter
    @Builder
    public static class Created {
        private final Long orderId;
        private final LocalDateTime orderDate;

        public static Created of(Order order) {
            return Created.builder()
                    .orderId(order.getId())
                    .orderDate(order.getOrderDate())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Completed {
        private final Long orderId;
        private final LocalDateTime completedDate;

        public static Completed of(Order order) {
            return Completed.builder()
                    .orderId(order.getId())
                    .completedDate(LocalDateTime.now())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Failed {
        private final Long orderId;
        private final String reason;

        public static Failed of(Order order, String reason) {
            return Failed.builder()
                    .orderId(order.getId())
                    .reason(reason)
                    .build();
        }
    }
}
