package kr.hhplus.be.server.order.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    ORDERED("주문진행"),
    PAID("주문완료"),
    FAILED("주문실패");

    private final String description;
}
