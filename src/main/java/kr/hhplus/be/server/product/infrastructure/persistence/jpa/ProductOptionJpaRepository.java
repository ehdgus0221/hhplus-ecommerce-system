package kr.hhplus.be.server.product.infrastructure.persistence.jpa;

import kr.hhplus.be.server.product.domain.model.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProductOptionJpaRepository extends JpaRepository<ProductOption, Long> {
    Optional<ProductOption> findById(Long id);
}
