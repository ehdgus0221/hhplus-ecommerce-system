package kr.hhplus.be.server.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.transform.AutoImplement;
import kr.hhplus.be.server.balance.domain.model.Balance;
import kr.hhplus.be.server.balance.domain.repository.BalanceRepository;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.model.UserCouponStatus;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.order.api.dto.request.OrderRequestDto;
import kr.hhplus.be.server.order.api.dto.response.OrderResponseDto;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.model.OrderStatus;
import kr.hhplus.be.server.order.infrastructure.persistence.jpa.OrderJpaRepository;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.model.ProductOptionStatus;
import kr.hhplus.be.server.product.domain.model.ProductStatus;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestcontainersConfiguration.class)
public class OrderIntegrationTest {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;


    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("주문 정상 성공 케이스")
    void order_success_case() throws Exception {
        // 1. 상품 및 옵션 등록
        Product product = Product.create("신발", 10_000,"신발입니다.");
                //new Product("신발", 10_000, "a", ProductStatus.ON_SALE);
        productRepository.save(product);

        ProductOption option = new ProductOption("검정색", 10_000, 100, product, ProductOptionStatus.ON_SALE);
        productOptionRepository.save(option); // 옵션 저장 추가

        // 2. 유저 잔액 등록
        Long userId = 1L;
        Balance balance = Balance.createInitial(userId);
        balance.addAmount(100_000L);
        balanceRepository.save(balance);

        // 3. 쿠폰 등록 및 유저에 지급
        Coupon coupon = Coupon.create("10% 할인", 10, 10, 100, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        couponRepository.save(coupon); // 쿠폰 저장 추가

        UserCoupon userCoupon = new UserCoupon(coupon.getId(), userId, coupon, UserCouponStatus.UNUSED, LocalDateTime.now(), LocalDateTime.now());
        userCouponRepository.save(userCoupon);
        Long userCouponId = userCoupon.getId();

        // 4. 주문 요청 생성
        OrderRequestDto.Create request = new OrderRequestDto.Create(
                product.getId(),
                option.getId(),
                2,
                userId,
                userCouponId
        );

        // 5. 요청 실행
        MvcResult result = mockMvc.perform(post("/api/orders") // 실제 엔드포인트로 바꿔주세요
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // 6. 결과를 Map<String, Object>로 받아서 출력 및 검증
        String responseBody = result.getResponse().getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});

        System.out.println("응답 데이터: " + responseMap);

        assertThat(responseMap.get("orderId")).isNotNull();
        assertThat(Long.valueOf(responseMap.get("productId").toString())).isEqualTo(product.getId());
        assertThat(Long.valueOf(responseMap.get("optionId").toString())).isEqualTo(option.getId());
        assertThat(Integer.valueOf(responseMap.get("stock").toString())).isEqualTo(2);
        assertThat(responseMap.get("status")).isEqualTo("PAID");

        // 7. 실제 DB 확인
        Long orderId = Long.valueOf(responseMap.get("orderId").toString());

        Order order = orderJpaRepository.findById(orderId)
                .orElseThrow();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getOrderItems().get(0).getProductOptionId()).isEqualTo(option.getId());

        // 8. 잔액 차감 확인
        Balance afterBalance = balanceRepository.findByUserId(userId).orElseThrow();
        long totalPrice = option.getPrice() * 2 * 90 / 100; // 10% 할인 적용
        assertThat(afterBalance.getAmount()).isEqualTo(100_000L - totalPrice);

    }


}
