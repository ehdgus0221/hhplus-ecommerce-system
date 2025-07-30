package kr.hhplus.be.server.coupon.domain.repository;

import kr.hhplus.be.server.coupon.domain.model.Coupon;


public interface CouponRepository {
    Coupon findById(Long couponId);
    Coupon save(Coupon userCoupon);
}
