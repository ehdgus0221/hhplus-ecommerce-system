package kr.hhplus.be.server.coupon.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private boolean used;

    private LocalDateTime issuedAt;

    public static UserCoupon issue(Long userId, Coupon coupon) {
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.userId = userId;
        userCoupon.coupon = coupon;
        userCoupon.used = false;
        userCoupon.issuedAt = LocalDateTime.now();

        return userCoupon;
    }
}
