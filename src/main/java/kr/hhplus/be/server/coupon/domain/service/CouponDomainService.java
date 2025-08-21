package kr.hhplus.be.server.coupon.domain.service;

import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.api.dto.response.CouponUseResponseDto;
import kr.hhplus.be.server.coupon.api.dto.response.UserCouponResponseDto;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.model.CouponStatus;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRedisRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CouponDomainService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserCouponRedisRepository redisRepository;

    /**
     * Redis 후보자 등록
     */
    public UserCouponResponseDto registerCandidate(Long userId, Long couponId) {
        long timestamp = System.currentTimeMillis();
        boolean added = redisRepository.addCandidate(couponId, userId, timestamp);
        if (!added) {
            throw new IllegalStateException("이미 발급 요청한 쿠폰입니다.");
        }
        return UserCouponResponseDto.pending(userId, couponId);
    }

    /**
     * 스케줄러 배치 발급
     */
    public void batchIssue() {
        List<Coupon> activeCoupons = couponRepository.findByStatus(CouponStatus.START);

        for (Coupon coupon : activeCoupons) {
            int issuedCount = userCouponRepository.countByCouponId(coupon.getId());
            int remaining = coupon.getQuantity() - issuedCount;
            if (remaining <= 0) continue;

            Set<Long> candidates = redisRepository.getCandidates(coupon.getId(), remaining);
            if (candidates.isEmpty()) continue;

            List<UserCoupon> userCoupons = candidates.stream()
                    .map(userId -> UserCoupon.issue(userId, coupon))
                    .toList();

            userCouponRepository.saveAll(userCoupons);
            redisRepository.removeCandidates(coupon.getId(), new ArrayList<>(candidates));
        }
    }

    public CouponUseResponseDto useUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId);
        userCoupon.use();

        return CouponUseResponseDto.from(userCoupon);
    }

    public void finishCoupons() {
        List<Coupon> activeCoupons = couponRepository.findByStatus(CouponStatus.START);
        for (Coupon coupon : activeCoupons) {
            int issuedCount = userCouponRepository.countByCouponId(coupon.getId());
            if (issuedCount >= coupon.getQuantity()) {
                coupon.finish();
            }
        }
    }
}
