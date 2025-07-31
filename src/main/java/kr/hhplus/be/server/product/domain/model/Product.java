package kr.hhplus.be.server.product.domain.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.ErrorMessages;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;
    private int basePrice;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProductOption> options = new ArrayList<>();
    @Builder
    private Product(String name, int basePrice, String description, ProductStatus status) {

        if (basePrice <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_PRODUCT_PRICE);
        }

        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.status = status;
    }

}
