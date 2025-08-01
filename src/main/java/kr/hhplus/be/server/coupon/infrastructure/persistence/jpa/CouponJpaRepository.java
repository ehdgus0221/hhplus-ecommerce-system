package kr.hhplus.be.server.coupon.infrastructure.persistence.jpa;

import kr.hhplus.be.server.coupon.domain.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CouponJpaRepository extends JpaRepository<Coupon,Long> {
}
