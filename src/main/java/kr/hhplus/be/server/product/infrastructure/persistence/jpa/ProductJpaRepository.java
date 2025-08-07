package kr.hhplus.be.server.product.infrastructure.persistence.jpa;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<Product,Long> {
    List<Product> findByStatus(ProductStatus status);
    Optional<Product> findById(Long productId);
    //Product findByIdOrThrow(Long id);
}
