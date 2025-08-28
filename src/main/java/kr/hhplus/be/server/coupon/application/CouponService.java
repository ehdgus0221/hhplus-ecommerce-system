package kr.hhplus.be.server.coupon.application;

import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.api.dto.response.CouponUseResponseDto;
import kr.hhplus.be.server.coupon.api.dto.response.UserCouponResponseDto;
import kr.hhplus.be.server.coupon.domain.service.CouponDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponDomainService couponDomainService;


    /**
     * 사용자 요청 시 Redis 후보자로 등록
     */
    @Transactional
    public UserCouponResponseDto issue(CouponIssueRequestDto request) {
        return couponDomainService.registerCandidate(request.getUserId(), request.getCouponId());
    }

    /**
     * 스케줄러에서 DB 반영
     */
    @Transactional
    public void batchIssueCoupons() {
        couponDomainService.batchIssue();
    }

    @Transactional
    public CouponUseResponseDto use(Long couponId) {
        return couponDomainService.useUserCoupon(couponId);
    }

    /**
     * 쿠폰 발급 완료(수량 소진한 경우)
     */
    @Transactional
    public void finishCoupons() {
        couponDomainService.finishCoupons();
    }


}
