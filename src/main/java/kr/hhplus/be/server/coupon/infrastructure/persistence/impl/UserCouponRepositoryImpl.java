package kr.hhplus.be.server.coupon.infrastructure.persistence.impl;

import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.infrastructure.persistence.jpa.UserCouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final UserCouponJpaRepository userCouponJpaRepository;

    @Override
    public UserCoupon findById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId)
                .orElseThrow(() -> new NotFoundException("사용자 정보를 찾을 수 없습니다."));
    }

    @Override
    public boolean existsByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponJpaRepository.existsByUserIdAndCouponId(userId, couponId);
    };

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public int countByCouponId(Long couponId) {
        return userCouponJpaRepository.countByCouponId(couponId);
    }

    @Override
    public List<UserCoupon> saveAll(Iterable<UserCoupon> userCoupons) {
        return userCouponJpaRepository.saveAll(userCoupons);
    }
}
