package kr.hhplus.be.server.product.api.dto.response;


import kr.hhplus.be.server.product.domain.model.ProductPopular;

public record ProductPopularResponseDto(Long productId, Integer salesCount) {

    public static ProductPopularResponseDto from(ProductPopular productPopular) {
        return new ProductPopularResponseDto(
                productPopular.productId(),
                productPopular.salesCount()
        );
    }
}
