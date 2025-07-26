package kr.hhplus.be.server.order.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문 (외래키)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 상품 옵션 아이디 (외래키)
    private Long productOptionId;

    private Integer stock;

    private Integer unitPrice;

    public static OrderItem create(Long productOptionId, Integer stock, Integer unitPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.productOptionId = productOptionId;
        orderItem.stock = stock;
        orderItem.unitPrice = unitPrice;
        return orderItem;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
