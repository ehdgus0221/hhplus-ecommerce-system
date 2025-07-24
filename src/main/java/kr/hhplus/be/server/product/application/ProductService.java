package kr.hhplus.be.server.product.application;

import kr.hhplus.be.server.product.api.dto.ProductDtoMapper;
import kr.hhplus.be.server.product.api.dto.response.ProductDetailResponseDto;
import kr.hhplus.be.server.product.api.dto.response.ProductResponseDto;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.service.ProductDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductDomainService productDomainService;
    private final ProductDtoMapper productMapper;

    public List<ProductResponseDto> getAllProducts() {
        return productDomainService.getAllActiveProducts().stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = productDomainService.getProductWithOptions(productId);
        return productMapper.toDetailResponse(product);
    }
}
