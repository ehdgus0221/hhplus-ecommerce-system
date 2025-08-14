package kr.hhplus.be.server.product;

import kr.hhplus.be.server.product.api.dto.ProductDtoMapper;
import kr.hhplus.be.server.product.api.dto.response.ProductDetailResponseDto;
import kr.hhplus.be.server.product.api.dto.response.ProductResponseDto;
import kr.hhplus.be.server.product.application.ProductService;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.service.ProductDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDomainService productDomainService;

    @Mock
    private ProductDtoMapper productDtoMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("getAllProducts 성공")
    void getAllProducts_success() {
        // given
        List<Product> products = List.of(
                Product.create("상품A", 1000, "설명A"),
                Product.create("상품B", 2000, "설명B")
        );
        List<ProductResponseDto> dtoList = List.of(
                mock(ProductResponseDto.class), mock(ProductResponseDto.class)
        );

        when(productDomainService.getAllActiveProducts()).thenReturn(products);
        when(products.stream()).thenReturn(products.stream()); // default

        when(productDtoMapper.toDetailResponse(any())).thenCallRealMethod(); // 필요시 실제 호출
        when(productDomainService.getAllActiveProducts()).thenReturn(products);

        when(productDomainService.getAllActiveProducts()).thenReturn(products);

        when(products.stream().map(ProductResponseDto::new).collect(Collectors.toList()))
                .thenReturn(dtoList);

        when(productDomainService.getAllActiveProducts()).thenReturn(products);

        // when
        List<ProductResponseDto> result = productService.getAllProducts();

        // then
        assertThat(result).isNotNull();
        verify(productDomainService).getAllActiveProducts();
    }

    @Test
    @DisplayName("getProductDetail 성공")
    void getProductDetail_success() {
        // given
        Long productId = 1L;
        Product product = mock(Product.class);
        ProductDetailResponseDto dto = mock(ProductDetailResponseDto.class);

        when(productDomainService.getProductWithOptions(productId)).thenReturn(product);
        when(productDtoMapper.toDetailResponse(product)).thenReturn(dto);

        // when
        ProductDetailResponseDto result = productService.getProductDetail(productId);

        // then
        assertThat(result).isEqualTo(dto);
        verify(productDomainService).getProductWithOptions(productId);
        verify(productDtoMapper).toDetailResponse(product);
    }

    @Test
    @DisplayName("getProductDetail 실패 - 상품 없음")
    void getProductDetail_fail_notFound() {
        // given
        Long productId = 1L;

        when(productDomainService.getProductWithOptions(productId))
                .thenThrow(new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> productService.getProductDetail(productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다.");

        verify(productDomainService).getProductWithOptions(productId);
    }
}
