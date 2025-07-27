package kr.hhplus.be.server.coupon.domain.service;

import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
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
        Coupon coupon = couponRepository.findByIdOrThrow(request.getCouponId());

        // 유효성 검사: 기간, 수량, 중복 여부
        if (coupon.isExpired()) {
            throw new IllegalStateException("쿠폰이 만료되었습니다.");
        }

        if (coupon.getQuantity() <= 0) {
            throw new IllegalStateException("쿠폰 수량이 없습니다.");
        }

        if (userCouponRepository.existsByUserIdAndCouponId(request.getUserId(), request.getCouponId())) {
            throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
        }

        // 쿠폰 발급 처리
        coupon.decreaseQuantity();
        couponRepository.save(coupon);

        UserCoupon userCoupon = UserCoupon.issue(request.getUserId(), coupon);
        userCouponRepository.save(userCoupon);

        return UserCouponResponseDto.from(userCoupon);
    }
}
