package kr.hhplus.be.server.product.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 양방향 연관관계
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Version
    private Long version;

    @Builder
    public ProductOption(String optionName, Integer price, Integer stock, boolean isActive) {
        this.optionName = optionName;
        this.price = price;
        this.stock = stock;
        this.isActive = isActive;
    }

    // 연관관계 설정
    public void setProduct(Product product) {
        this.product = product;
    }

    public void decreaseStock(int amount) {
        if (!isActive) {
            throw new IllegalStateException("비활성화된 옵션입니다.");
        }
        if (stock < amount) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stock -= amount;
    }

    public boolean isOutOfStock() {
        return this.stock == 0;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public void decreaseStock(Integer stock) {
        if (this.stock < stock) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stock -= stock;
    }

    public void increaseStock(Integer stock) {
        this.stock += stock;
    }
}