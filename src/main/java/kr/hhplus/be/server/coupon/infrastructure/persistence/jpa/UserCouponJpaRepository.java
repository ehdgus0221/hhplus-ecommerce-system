package kr.hhplus.be.server.coupon.infrastructure.persistence.jpa;

import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon,Long> {
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
}
