package kr.hhplus.be.server.coupon.api.dto.response;

import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.model.CouponStatus;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.model.UserCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUseResponseDto {

    private Long userCouponId;
    private UserCouponStatus couponStatus;
    private LocalDateTime usedAt;

    public static CouponUseResponseDto from(UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();

        return CouponUseResponseDto.builder()
                .userCouponId(userCoupon.getId())
                .couponStatus(userCoupon.getUsedStatus())
                .usedAt(userCoupon.getUsedAt())
                .build();
    }
}