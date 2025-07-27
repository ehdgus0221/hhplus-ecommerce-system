package kr.hhplus.be.server.product.api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductDetailResponseDto {
    private Long id;
    private String name;
    private int basePrice;
    private String description;
    private List<ProductOptionResponse> options;

    @Getter
    @AllArgsConstructor
    public static class ProductOptionResponse {
        private Long id;
        private String optionName;
        private int price;
        private int stock;
        private boolean isActive;
        private boolean isSoldOut;  // 재고 없으면 true
    }
}
