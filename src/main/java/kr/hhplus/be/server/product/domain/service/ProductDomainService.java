package kr.hhplus.be.server.product.domain.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.model.ProductPopular;
import kr.hhplus.be.server.product.domain.model.ProductStatus;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductDomainService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LAST_3_DAYS_KEY = "sales:last3days";

    public List<Product> getAllActiveProducts() {
        return productRepository.findByStatus(ProductStatus.ON_SALE);
    }

    public Product getProductWithOptions(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // LAZY 로딩 대비: 옵션 접근 시 예외 방지 or 직접 Fetch Join 조회로 대체 가능
        product.getOptions().size();

        return product;
    }

    public void decreaseOptionStock(Long productOptionId, int stock) {
        ProductOption option = productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new EntityNotFoundException("상품 옵션이 존재하지 않습니다."));

        option.decreaseStock(stock); // 도메인 메서드 호출
    }

    public List<ProductPopular> getPopularProducts() {
        Set<ZSetOperations.TypedTuple<Object>> results =
                redisTemplate.opsForZSet().reverseRangeWithScores(LAST_3_DAYS_KEY, 0, 4);

        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(r -> new ProductPopular(
                        Long.valueOf(r.getValue().toString()), // productId
                        r.getScore().intValue()               // salesCount
                ))
                .toList();
    }

}
