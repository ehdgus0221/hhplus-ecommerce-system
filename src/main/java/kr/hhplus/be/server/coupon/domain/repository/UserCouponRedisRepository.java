package kr.hhplus.be.server.coupon.domain.repository;

import java.util.List;
import java.util.Set;

public interface UserCouponRedisRepository {
    boolean addCandidate(Long couponId, Long userId, long timestamp);
    Set<Long> getCandidates(Long couponId, int count);
    void removeCandidates(Long couponId, List<Long> userIds);
}
