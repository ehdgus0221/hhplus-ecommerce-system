package kr.hhplus.be.server.product.api.dto;

import kr.hhplus.be.server.product.api.dto.response.ProductDetailResponseDto;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductDtoMapper {
    public ProductDetailResponseDto toDetailResponse(Product product) {
        return new ProductDetailResponseDto(
                product.getId(),
                product.getName(),
                product.getBasePrice(),
                product.getDescription(),
                product.getOptions().stream()
                        .filter(ProductOption::isActive)
                        .map(this::toOptionResponse)
                        .collect(Collectors.toList())
        );
    }

    private ProductDetailResponseDto.ProductOptionResponse toOptionResponse(ProductOption option) {
        return new ProductDetailResponseDto.ProductOptionResponse(
                option.getId(),
                option.getOptionName(),
                option.getPrice(),
                option.getStock(),
                option.isActive(),
                option.getStock() <= 0
        );
    }
}
