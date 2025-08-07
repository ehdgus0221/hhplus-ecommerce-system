package kr.hhplus.be.server.product.domain.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.ErrorMessages;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    private String optionName;

    private Integer price;

    private long stock;

    @Enumerated(EnumType.STRING)
    private ProductOptionStatus status;

    @Builder
    public ProductOption(String optionName, Integer price, long stock, Product product, ProductOptionStatus status) {
        this.optionName = optionName;
        this.price = price;
        this.stock = stock;
        this.product = product;
        this.status = status;
    }

    public static ProductOption create(Product product, String optionName, int price, long stock) {
        return ProductOption.builder()
                .product(product)
                .optionName(optionName)
                .price(price)
                .stock(stock)
                .status(ProductOptionStatus.ON_SALE)
                .build();
    }

    public void decreaseStock(long amount) {
        if (!ProductOptionStatus.ON_SALE.equals(this.status)) {
            throw new IllegalStateException(ErrorMessages.PRODUCT_OPTION_INACTIVE);
        }
        if (stock < amount) {
            throw new IllegalStateException(ErrorMessages.PRODUCT_OPTION_NOT_FOUND);
        }
        this.stock -= amount;
    }

    public boolean isOutOfStock() {
        return this.stock == 0;
    }

    public boolean isStockInsufficient(long quantity) {
        return this.stock < quantity;
    }

    public void increaseStock(long stock) {
        this.stock += stock;
    }

    public boolean isActive() {
        return ProductOptionStatus.ON_SALE.equals(this.status);
    }
}