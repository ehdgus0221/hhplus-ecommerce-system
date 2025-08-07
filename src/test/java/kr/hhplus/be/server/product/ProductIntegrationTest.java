package kr.hhplus.be.server.product;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.product.api.dto.response.ProductDetailResponseDto;
import kr.hhplus.be.server.product.api.dto.response.ProductResponseDto;
import kr.hhplus.be.server.product.application.ProductService;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.model.ProductOptionStatus;
import kr.hhplus.be.server.product.domain.model.ProductStatus;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
class ProductIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;


    @Test
    void getAllProducts_활성화된상품만조회된다() {
        // given
        Product activeProduct = Product.builder()
                .name("활성 상품")
                .basePrice(10000)
                .description("판매 중인 상품입니다.")
                .status(ProductStatus.ON_SALE)
                .build();

        Product inactiveProduct = Product.builder()
                .name("비활성 상품")
                .basePrice(20000)
                .description("판매 종료된 상품입니다.")
                .status(ProductStatus.DISCONTINUED)
                .build();

        ProductOption option1 = ProductOption.builder()
                .optionName("옵션 A")
                .price(1000)
                .stock(10)
                .product(activeProduct)
                .status(ProductOptionStatus.ON_SALE)
                .build();

        ProductOption option2 = ProductOption.builder()
                .optionName("옵션 B")
                .price(2000)
                .stock(0)
                .product(activeProduct)
                .status(ProductOptionStatus.ON_SALE)
                .build();

        ProductOption option3 = ProductOption.builder()
                .optionName("옵션 C")
                .price(1500)
                .stock(5)
                .product(inactiveProduct)
                .status(ProductOptionStatus.ON_SALE)
                .build();

        // 연관관계 세팅
        activeProduct.getOptions().add(option1);
        activeProduct.getOptions().add(option2);
        inactiveProduct.getOptions().add(option3);

        productRepository.save(activeProduct);
        productRepository.save(inactiveProduct);

        // when
        List<ProductResponseDto> result = productService.getAllProducts();

        // then
        assertThat(result).hasSize(1);
        ProductResponseDto dto = result.get(0);
        assertThat(dto.getName()).isEqualTo("활성 상품");
        assertThat(dto.getOptions()).hasSize(1); // 재고 없는 옵션은 필터링
        assertThat(dto.getOptions().get(0).getOptionName()).isEqualTo("옵션 A");
    }


    @Test
    @Transactional
    void getProductDetail_정상조회된다() {
        // given
        Product product = Product.builder()
                .name("디테일 상품")
                .basePrice(30000)
                .description("상품 상세 조회용")
                .status(ProductStatus.ON_SALE)
                .build();

        ProductOption option = ProductOption.builder()
                .optionName("옵션 X")
                .price(5000)
                .stock(20)
                .product(product) // 양방향 연관관계 설정
                .status(ProductOptionStatus.ON_SALE)
                .build();

        product.getOptions().add(option); // product → options 추가


        productRepository.save(product);

        // when
        ProductDetailResponseDto detail = productService.getProductDetail(product.getId());

        // then
        assertThat(detail.getName()).isEqualTo("디테일 상품");
        assertThat(detail.getOptions()).hasSize(1);

        ProductDetailResponseDto.ProductOptionResponse optionDto = detail.getOptions().get(0);
        assertThat(optionDto.getOptionName()).isEqualTo("옵션 X");
        assertThat(optionDto.getStock()).isEqualTo(20);
        assertThat(optionDto.isActive()).isTrue();
        assertThat(optionDto.isSoldOut()).isFalse();
    }


}

