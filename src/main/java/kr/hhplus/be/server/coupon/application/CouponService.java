package kr.hhplus.be.server.coupon.application;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.api.dto.response.CouponUseResponseDto;
import kr.hhplus.be.server.coupon.api.dto.response.UserCouponResponseDto;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.domain.service.CouponDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponDomainService couponDomainService;
    private final CouponRepository couponRepository;

    @Transactional
    public UserCouponResponseDto issue(CouponIssueRequestDto request) {
        return couponDomainService.issueCoupon(request);
    }

    @Transactional
    public CouponUseResponseDto use(Long couponId) {
        return couponDomainService.useUserCoupon(couponId);
    }


}
