package kr.hhplus.be.server.coupon.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    private UserCouponStatus usedStatus;

    private LocalDateTime issuedAt;

    private LocalDateTime usedAt;

    @Builder
    private UserCoupon(Long id, Long userId, Coupon coupon, UserCouponStatus usedStatus,
                       LocalDateTime issuedAt, LocalDateTime usedAt) {
        this.id = id;
        this.userId = userId;
        this.coupon = coupon;
        this.usedStatus = usedStatus;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
    }

    public static UserCoupon issue(Long userId, Coupon coupon) {
        return issue(userId, coupon, LocalDateTime.now());
    }

    public static UserCoupon issue(Long userId, Coupon coupon, LocalDateTime issuedAt) {
        return UserCoupon.builder()
                .userId(userId)
                .coupon(coupon)
                .usedStatus(UserCouponStatus.UNUSED) // 기본 상태는 미사용
                .issuedAt(issuedAt)
                .build();
    }

    public void use() {
        // 발급가능 상태일 때만 사용 가능
        if (!coupon.getStatus().equals(CouponStatus.START)) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }
        this.usedStatus = UserCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }
}
