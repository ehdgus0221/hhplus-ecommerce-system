package kr.hhplus.be.server.coupon.domain.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.ErrorMessages;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    private String name;

    private int discountRate;
    private int minUsePrice;

    private int totalQuantity;
    private int quantity; // 남은 수량

    private LocalDateTime issuanceStartTime;
    private LocalDateTime issuanceEndTime;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime expiredAt;

    @Builder
    private Coupon(Long id, String name, int discountRate, int minUsePrice, int totalQuantity, int quantity,
                   LocalDateTime issuanceStartTime, LocalDateTime issuanceEndTime, CouponStatus status, LocalDateTime expiredAt) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.minUsePrice = minUsePrice;
        this.totalQuantity = totalQuantity;
        this.quantity = quantity;
        this.issuanceStartTime = issuanceStartTime;
        this.issuanceEndTime = issuanceEndTime;
        this.status = status;
        this.expiredAt = expiredAt;
    }

    public static Coupon create(String name, int discountRate, int minUsePrice, int totalQuantity,
                                       LocalDateTime issuanceStartTime, LocalDateTime issuanceEndTime, LocalDateTime expiredAt) {
        if (totalQuantity < 0) {
            throw new IllegalArgumentException(ErrorMessages.COUPON_QUANTITY_INVALID);
        }
        if (discountRate < 1 || discountRate > 100) {
            throw new IllegalArgumentException(ErrorMessages.COUPON_DISCOUNT_RATE_INVALID);
        }
        if (expiredAt == null || expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(ErrorMessages.COUPON_EXPIRED_AT_INVALID);
        }

        return Coupon.builder()
                .name(name)
                .discountRate(discountRate)
                .minUsePrice(minUsePrice)
                .totalQuantity(totalQuantity)
                .quantity(totalQuantity)
                .issuanceStartTime(issuanceStartTime)
                .issuanceEndTime(issuanceEndTime)
                .status(CouponStatus.START)
                .expiredAt(expiredAt)
                .build();
    }

    public void decreaseQuantity() {
        if (quantity <= 0) {
            throw new IllegalStateException(ErrorMessages.COUPON_OUT_OF_STOCK);
        }
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(ErrorMessages.COUPON_EXPIRED);
        }
        this.quantity -= 1;
    }

}