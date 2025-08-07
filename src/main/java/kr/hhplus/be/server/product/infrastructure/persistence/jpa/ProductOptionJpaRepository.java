package kr.hhplus.be.server.product.infrastructure.persistence.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ProductOptionJpaRepository extends JpaRepository<ProductOption, Long> {
    Optional<ProductOption> findById(Long id);

    // 동시성 제어
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ProductOption> findWithLockById(Long id);
}
