package kr.hhplus.be.server.product.domain.repository;

import kr.hhplus.be.server.product.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAllActiveProducts();
    Optional<Product> findById(Long productId);
    Product findByIdOrThrow(Long id);
    Product save(Product product);
}
