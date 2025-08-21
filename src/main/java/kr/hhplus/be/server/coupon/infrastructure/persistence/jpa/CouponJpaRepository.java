package kr.hhplus.be.server.coupon.infrastructure.persistence.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.model.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;


public interface CouponJpaRepository extends JpaRepository<Coupon,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findWithLockById(Long couponId);
    List<Coupon> findByStatus(CouponStatus status);
}
