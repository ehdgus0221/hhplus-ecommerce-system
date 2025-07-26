package kr.hhplus.be.server.coupon.application;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.api.dto.response.UserCouponResponseDto;
import kr.hhplus.be.server.coupon.domain.service.CouponDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponDomainService couponDomainService;

    @Transactional
    public UserCouponResponseDto issueCoupon(CouponIssueRequestDto request) {
        // 1. 도메인 서비스에 쿠폰 발급 요청
        return couponDomainService.issueCoupon(request);
        // 2. 기타 트랜잭션 내 여러 작업(예: 알림 발송, 로깅 등)
    }
}
