package kr.hhplus.be.server.product.domain.repository;

import kr.hhplus.be.server.product.domain.model.ProductOption;

import java.util.Optional;

public interface ProductOptionRepository {
    Optional<ProductOption> findById(Long id);
    ProductOption findWithPessimisticLock(Long id);
}
