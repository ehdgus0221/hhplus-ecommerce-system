package kr.hhplus.be.server.coupon.domain.service;

import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.api.dto.response.CouponUseResponseDto;
import kr.hhplus.be.server.coupon.api.dto.response.UserCouponResponseDto;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponDomainService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public UserCouponResponseDto issueCoupon(CouponIssueRequestDto request) {
        Coupon coupon = couponRepository.findById(request.getCouponId());
        if (userCouponRepository.existsByUserIdAndCouponId(request.getUserId(), request.getCouponId())) {
            throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
        }
        // 쿠폰 발급 처리
        coupon.decreaseQuantity();
        // 발급된 사용자 쿠폰 저장
        UserCoupon userCoupon = UserCoupon.issue(request.getUserId(), coupon);
        userCouponRepository.save(userCoupon);

        return UserCouponResponseDto.from(userCoupon);
    }

    public CouponUseResponseDto useUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId);
        userCoupon.use();

        return CouponUseResponseDto.from(userCoupon);
    }
}
