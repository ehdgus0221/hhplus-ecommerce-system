package kr.hhplus.be.server.product.domain.repository;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findByStatus(ProductStatus status);
    Optional<Product> findById(Long productId);
    Product findByIdOrThrow(Long id);
    Product save(Product product);
}
