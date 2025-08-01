package kr.hhplus.be.server.coupon.domain.repository;

import kr.hhplus.be.server.coupon.domain.model.UserCoupon;

public interface UserCouponRepository {
    UserCoupon findById(Long userCouponId);
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
    UserCoupon save(UserCoupon userCoupon);
}
