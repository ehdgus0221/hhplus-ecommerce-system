package kr.hhplus.be.server.product.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    ON_SALE("판매중"),
    DISCONTINUED("판매종료");
    private final String description;

}
