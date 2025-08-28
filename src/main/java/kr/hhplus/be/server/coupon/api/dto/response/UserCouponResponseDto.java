package kr.hhplus.be.server.coupon.api.dto.response;

import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCouponResponseDto {

    private Long userCouponId;
    private Long userId;
    private Long couponId;
    private String couponName;
    private Integer discountRate;
    private Boolean used;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;

    public static UserCouponResponseDto from(UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();

        return UserCouponResponseDto.builder()
                .userCouponId(userCoupon.getId())
                .userId(userCoupon.getUserId())
                .couponId(coupon.getId())
                .couponName(coupon.getName())
                .discountRate(coupon.getDiscountRate())
                .issuedAt(userCoupon.getIssuedAt())
                .expiredAt(coupon.getExpiredAt())
                .build();
    }

    // 쿠폰 발급 대기중 dto
    public static UserCouponResponseDto pending(Long userId, Long couponId) {
        return UserCouponResponseDto.builder()
                .userId(userId)
                .couponId(couponId)
                .used(false)        // 아직 사용 전이므로 false
                .build();
    }
}