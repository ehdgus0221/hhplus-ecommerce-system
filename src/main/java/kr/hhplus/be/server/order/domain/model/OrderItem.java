package kr.hhplus.be.server.order.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productOptionId;

    private String productOptionName;

    private long stock;

    private long unitPrice;

    @Builder
    private OrderItem(Long id, Order order, Long productOptionId, String productOptionName, long stock, long unitPrice) {
        this.id = id;
        this.order = order;
        this.productOptionId = productOptionId;
        this.productOptionName = productOptionName;
        this.stock = stock;
        this.unitPrice = unitPrice;
    }

    public static OrderItem create(Long productOptionId, String productOptionName, long stock, long unitPrice) {
        return OrderItem.builder()
                .productOptionId(productOptionId)
                .productOptionName(productOptionName)
                .stock(stock)
                .unitPrice(unitPrice)
                .build();
    }

    public void setOrder(Order order) {
        this.order = order;
    }


}
