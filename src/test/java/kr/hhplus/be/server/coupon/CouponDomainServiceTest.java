package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.api.dto.response.UserCouponResponseDto;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.domain.service.CouponDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponDomainServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponDomainService couponDomainService;

    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueCoupon_success() {
        // given
        Long userId = 1L;
        Long couponId = 10L;

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .name("할인쿠폰")
                .discountRate(20)
                .quantity(5)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();

        when(couponRepository.findById(couponId)).thenReturn(coupon);
        when(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(false);
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(userCouponRepository.save(any(UserCoupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponIssueRequestDto request = new CouponIssueRequestDto();
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "couponId", couponId);

        // when
        UserCouponResponseDto response = couponDomainService.issueCoupon(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getCouponId()).isEqualTo(couponId);
        assertThat(response.getCouponName()).isEqualTo(coupon.getName());
        assertThat(response.getDiscountRate()).isEqualTo(coupon.getDiscountRate());
        assertThat(response.getUsed()).isFalse();
        assertThat(response.getExpiredAt()).isEqualTo(coupon.getExpiredAt());
        assertThat(coupon.getQuantity()).isEqualTo(4);  // 5 - 1 감소 확인

        verify(couponRepository).findById(couponId);
        verify(userCouponRepository).existsByUserIdAndCouponId(userId, couponId);
        verify(couponRepository).save(coupon);
        verify(userCouponRepository).save(any(UserCoupon.class));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 쿠폰 만료")
    void issueCoupon_fail_expired() {
        Long userId = 1L;
        Long couponId = 10L;

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .expiredAt(LocalDateTime.now().minusDays(1))  // 이미 만료
                .quantity(5)
                .build();

        when(couponRepository.findById(couponId)).thenReturn(coupon);

        CouponIssueRequestDto request = new CouponIssueRequestDto();
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "couponId", couponId);

        // when & then
        assertThatThrownBy(() -> couponDomainService.issueCoupon(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("쿠폰이 만료되었습니다.");

        verify(couponRepository).findById(couponId);
        verifyNoMoreInteractions(userCouponRepository, couponRepository);
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 수량 부족")
    void issueCoupon_fail_quantityEmpty() {
        Long userId = 1L;
        Long couponId = 10L;

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .quantity(0)  // 수량 없음
                .build();

        when(couponRepository.findById(couponId)).thenReturn(coupon);

        CouponIssueRequestDto request = new CouponIssueRequestDto();
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "couponId", couponId);

        assertThatThrownBy(() -> couponDomainService.issueCoupon(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("쿠폰 수량이 없습니다.");

        verify(couponRepository).findById(couponId);
        verifyNoMoreInteractions(userCouponRepository, couponRepository);
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 이미 발급받은 쿠폰")
    void issueCoupon_fail_alreadyIssued() {
        Long userId = 1L;
        Long couponId = 10L;

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .quantity(5)
                .build();

        when(couponRepository.findById(couponId)).thenReturn(coupon);
        when(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(true);  // 이미 발급됨

        CouponIssueRequestDto request = new CouponIssueRequestDto();
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "couponId", couponId);

        assertThatThrownBy(() -> couponDomainService.issueCoupon(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 발급받은 쿠폰입니다.");

        verify(couponRepository).findById(couponId);
        verify(userCouponRepository).existsByUserIdAndCouponId(userId, couponId);
        verifyNoMoreInteractions(couponRepository, userCouponRepository);
    }
}
