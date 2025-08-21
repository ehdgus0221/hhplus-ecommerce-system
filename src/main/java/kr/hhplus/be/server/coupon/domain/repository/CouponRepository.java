package kr.hhplus.be.server.coupon.domain.repository;

import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.model.CouponStatus;

import java.util.List;


public interface CouponRepository {
    Coupon findById(Long couponId);
    Coupon save(Coupon userCoupon);
    Coupon findWithLockById(Long couponId);
    List<Coupon> findByStatus(CouponStatus status);
}
