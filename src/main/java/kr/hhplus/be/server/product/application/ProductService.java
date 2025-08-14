package kr.hhplus.be.server.product.application;

import kr.hhplus.be.server.product.api.dto.ProductDtoMapper;
import kr.hhplus.be.server.product.api.dto.response.ProductDetailResponseDto;
import kr.hhplus.be.server.product.api.dto.response.ProductResponseDto;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.service.ProductDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductDomainService productDomainService;
    private final ProductDtoMapper productMapper;

    // 상품 추가/수정/삭제 메서드가 있었다면 해당 메서드에 무효화 처리를 해줘야함
    // ex) 상품 추가 메서드에 @CacheEvict(value = "products", key = "'all'")
    @Cacheable(value = "products", key = "'all'", cacheManager = "cacheManager")
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productDomainService.getAllActiveProducts().stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productDomainService.getProductWithOptions(productId);
        return productMapper.toDetailResponse(product);
    }
}
