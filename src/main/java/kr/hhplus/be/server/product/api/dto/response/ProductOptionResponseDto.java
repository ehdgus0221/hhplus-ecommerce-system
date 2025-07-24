package kr.hhplus.be.server.product.api.dto.response;


import kr.hhplus.be.server.product.domain.model.ProductOption;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class ProductOptionResponseDto {
    private Long id;
    private String optionName;
    private Integer price;
    private Integer stock;

    public ProductOptionResponseDto(ProductOption option) {
        this.id = option.getId();
        this.optionName = option.getOptionName();
        this.price = option.getPrice();
        this.stock = option.getStock();
    }
}
