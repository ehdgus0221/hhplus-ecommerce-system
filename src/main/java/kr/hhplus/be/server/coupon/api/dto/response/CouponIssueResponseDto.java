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
public class CouponIssueResponseDto {

    private Long userCouponId;
    private Long userId;
    private Long couponId;
    private String couponName;
    private int discountRate;
    private boolean used;
    private LocalDateTime issuedAt;

    public static CouponIssueResponseDto from(UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();

        return CouponIssueResponseDto.builder()
                .userCouponId(userCoupon.getId())
                .userId(userCoupon.getUserId())
                .couponId(coupon.getId())
                .couponName(coupon.getName())
                .discountRate(coupon.getDiscountRate())
                .issuedAt(userCoupon.getIssuedAt())
                .build();
    }
}