package kr.hhplus.be.server.order;

import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.order.domain.service.OrderDomainService;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.model.ProductOptionStatus;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDomainServiceTest {

    @InjectMocks
    private OrderDomainService orderDomainService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("상품 조회 동작 실패 - 상품이 존재하지 않을 때 예외 발생")
    void createOrder_productNotFound_fail() {
        Long userId = 1L;
        Long productId = 99L;
        Long optionId = 1L;
        Long couponId = 1L;
        int stock = 1;

        when(productRepository.findByIdOrThrow(productId))
                .thenThrow(new IllegalArgumentException("상품이 존재하지 않습니다."));

        assertThatThrownBy(() -> orderDomainService.createOrder(userId, productId, optionId, stock, couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("상품 옵션 조회 동작 실패 - 옵션이 존재하지 않을 때 예외 발생")
    void createOrder_optionNotFound_fail() {
        Long userId = 1L;
        Long productId = 2L;
        Long optionId = 99L;
        Long couponId = 1L;
        int stock = 1;

        Product product = Product.create("상품 1", 5000, "상품 1입니다.");
        ReflectionTestUtils.setField(product, "id", productId);

        when(productRepository.findByIdOrThrow(productId)).thenReturn(product);

        assertThatThrownBy(() -> orderDomainService.createOrder(userId, productId, optionId, stock, couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 옵션이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("옵션 활성화 상태 확인 동작 실패 - 비활성화된 옵션일 때 예외 발생")
    void createOrder_optionInactive_fail() {
        Long userId = 1L;
        Long productId = 2L;
        Long optionId = 3L;
        Long couponId = 1L;
        int stock = 1;

        Product product = Product.create("상품 2", 5000, "상품 2입니다.");

        ProductOption option = ProductOption.builder()
                .optionName("옵션1")
                .price(1000)
                .stock(10)
                .product(product)
                .status(ProductOptionStatus.ON_SALE)
                .build();


        ReflectionTestUtils.setField(product, "id", productId);
        ReflectionTestUtils.setField(option, "id", optionId);
        product.getOptions().add(option);

        when(productRepository.findByIdOrThrow(productId)).thenReturn(product);

        assertThatThrownBy(() -> orderDomainService.createOrder(userId, productId, optionId, stock, couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고가 부족하거나 비활성 상태입니다.");
    }

    @Test
    @DisplayName("옵션 재고 확인 동작 실패 - 재고 부족 시 예외 발생")
    void createOrder_optionStockInsufficient_fail() {
        Long userId = 1L;
        Long productId = 2L;
        Long optionId = 3L;
        Long couponId = 1L;
        int stock = 20;

        Product product = Product.create("상품 3", 5000, "상품 3입니다.");

        ProductOption option = ProductOption.builder()
                .optionName("옵션1")
                .price(1000)
                .stock(10)
                .product(product)
                .status(ProductOptionStatus.ON_SALE)
                .build();

        ReflectionTestUtils.setField(product, "id", productId);
        ReflectionTestUtils.setField(option, "id", optionId);
        product.getOptions().add(option);

        when(productRepository.findByIdOrThrow(productId)).thenReturn(product);

        assertThatThrownBy(() -> orderDomainService.createOrder(userId, productId, optionId, stock, couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고가 부족하거나 비활성 상태입니다.");
    }

    @Test
    @DisplayName("주문 생성 동작 성공 - 정상적인 주문 생성")
    void createOrder_success() {
        Long userId = 1L;
        Long productId = 2L;
        Long optionId = 3L;
        Long couponId = 1L;
        int stock = 2;

        Product product = Product.create("상품 3", 5000, "상품 3입니다.");

        ProductOption option = ProductOption.builder()
                .optionName("옵션")
                .price(1000)
                .stock(10)
                .product(product)
                .status(ProductOptionStatus.ON_SALE)
                .build();

        ReflectionTestUtils.setField(product, "id", productId);
        ReflectionTestUtils.setField(option, "id", optionId);

        product.getOptions().add(option);

        when(productRepository.findByIdOrThrow(productId)).thenReturn(product);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderDomainService.createOrder(userId, productId, optionId, stock, couponId);

        assertThat(order).isNotNull();
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getTotalPrice()).isEqualTo((product.getBasePrice() + option.getPrice()) * stock);
        assertThat(option.getStock()).isEqualTo(8);
    }
}
