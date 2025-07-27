package kr.hhplus.be.server.order.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long productId;

    private Long productOptionId;

    private Integer stock;

    private Long userCouponId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private int totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;

    public static Order create(Long userId, Long userCouponId, int totalPrice, OrderStatus status, LocalDateTime orderDate) {
        Order order = new Order();
        order.userId = userId;
        order.userCouponId = userCouponId;
        order.totalPrice = totalPrice;
        order.status = status;
        order.orderDate = orderDate;
        return order;
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void completePayment() {
        this.status = OrderStatus.PAID;
    }

    public void failPayment() {
        this.status = OrderStatus.FAILED;
    }
}
