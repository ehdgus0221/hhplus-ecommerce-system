package kr.hhplus.be.server.coupon.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueEvent;
import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.api.dto.response.CouponUseResponseDto;
import kr.hhplus.be.server.coupon.api.dto.response.UserCouponResponseDto;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.model.CouponStatus;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.order.domain.model.OutboxEvent;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRedisRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.order.domain.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final OutboxEventRepository outboxEventRepository;

    /**
     * Redis 후보자 등록
     */
    public UserCouponResponseDto registerCandidate(CouponIssueRequestDto request) throws JsonProcessingException {
        long timestamp = System.currentTimeMillis();
        boolean added = redisRepository.addCandidate(request.getCouponId(), request.getUserId(), System.currentTimeMillis());
        if (!added) {
            throw new IllegalStateException("이미 발급 요청한 쿠폰입니다.");
        }
        CouponIssueEvent event = new CouponIssueEvent(request.getUserId(), request.getCouponId());
        String payload = new ObjectMapper().writeValueAsString(event);

        outboxEventRepository.save(
                OutboxEvent.pending(
                        "coupon-events",   // topic 이름
                        request.getCouponId(),  // aggregateId
                        payload
                )
        );

        return UserCouponResponseDto.pending(request.getUserId(), request.getCouponId());
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
