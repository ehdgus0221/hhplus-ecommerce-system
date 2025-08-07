package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.api.dto.response.CouponUseResponseDto;
import kr.hhplus.be.server.coupon.api.dto.response.UserCouponResponseDto;
import kr.hhplus.be.server.coupon.application.CouponService;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.model.CouponStatus;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.model.UserCouponStatus;
import kr.hhplus.be.server.coupon.infrastructure.persistence.jpa.CouponJpaRepository;
import kr.hhplus.be.server.coupon.infrastructure.persistence.jpa.UserCouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
class CouponIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private UserCouponJpaRepository userCouponJpaRepository;

    private Coupon savedCoupon;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        savedCoupon = couponJpaRepository.save(Coupon.create(
                "10% 할인 쿠폰",
                10,
                1000,
                100,
                now.minusDays(1),
                now.plusDays(1),
                now.plusDays(7)
        ));
    }

    @Test
    @DisplayName("쿠폰 발급 - 성공")
    void issueCoupon_success() {
        CouponIssueRequestDto request = new CouponIssueRequestDto(userId, savedCoupon.getId());

        UserCouponResponseDto response = couponService.issue(request);

        assertEquals(userId, response.getUserId());
        assertEquals(savedCoupon.getId(), response.getCouponId());
        assertEquals(savedCoupon.getName(), response.getCouponName());
    }

    @Test
    @DisplayName("쿠폰 발급 - 중복 발급 실패")
    void issueCoupon_fail_duplicate() {
        CouponIssueRequestDto request = new CouponIssueRequestDto(userId, savedCoupon.getId());
        couponService.issue(request); // 최초 발급

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> couponService.issue(request)
        );
        assertEquals("이미 발급받은 쿠폰입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("쿠폰 사용 - 성공")
    void useCoupon_success() {
        // given
        CouponIssueRequestDto request = new CouponIssueRequestDto(userId, savedCoupon.getId());
        UserCouponResponseDto issued = couponService.issue(request);

        // when
        CouponUseResponseDto used = couponService.use(issued.getUserCouponId());

        // then
        assertEquals(UserCouponStatus.USED, used.getCouponStatus());
        assertNotNull(used.getUsedAt());
    }

}

