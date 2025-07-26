package kr.hhplus.be.server.coupon.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int discountRate;

    private int quantity; // 남은 수량

    private LocalDateTime expiredAt;

    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }

    public void decreaseQuantity() {
        if (quantity <= 0) {
            throw new IllegalStateException("쿠폰 수량이 부족합니다.");
        }
        this.quantity -= 1;
    }
}