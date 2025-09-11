package kr.hhplus.be.server.order.domain.model;

import lombok.Getter;

@Getter
public enum EventType {
    ORDER_CREATED,
    ORDER_COMPLETED,
    ORDER_COMPLETE_FAILED,
    PAYMENT_PAID,
    PAYMENT_FAILED,
    PAYMENT_CANCELED
}
