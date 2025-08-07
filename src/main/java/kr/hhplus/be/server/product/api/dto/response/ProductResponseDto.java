package kr.hhplus.be.server.product.api.dto.response;

import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductStatus;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private Integer basePrice;
    private String description;
    private List<ProductOptionResponseDto> options;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.basePrice = product.getBasePrice();
        this.description = product.getDescription();
        this.options = product.getOptions().stream()
                .filter(option -> option.getProduct().getStatus().equals(ProductStatus.ON_SALE) && option.getStock() > 0)
                .map(ProductOptionResponseDto::new)
                .collect(Collectors.toList());
    }
}
