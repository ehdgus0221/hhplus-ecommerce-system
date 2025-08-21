package kr.hhplus.be.server.coupon.infrastructure.persistence.impl;

import kr.hhplus.be.server.coupon.domain.repository.UserCouponRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserCouponRedisRepositoryImpl implements UserCouponRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "user_coupon:";

    @Override
    public boolean addCandidate(Long couponId, Long userId, long timestamp) {
        String key = KEY_PREFIX + couponId;
        Boolean added = redisTemplate.opsForZSet().addIfAbsent(key, userId.toString(), timestamp);
        redisTemplate.expire(key, Duration.ofDays(1));
        return Boolean.TRUE.equals(added);
    }

    @Override
    public Set<Long> getCandidates(Long couponId, int count) {
        String key = KEY_PREFIX + couponId;
        Set<Object> candidates = redisTemplate.opsForZSet().range(key, 0, count - 1);
        return candidates == null ? Collections.emptySet() :
                candidates.stream().map(o -> Long.valueOf(o.toString())).collect(Collectors.toSet());
    }

    @Override
    public void removeCandidates(Long couponId, List<Long> userIds) {
        String key = KEY_PREFIX + couponId;
        redisTemplate.opsForZSet().remove(key, userIds.stream().map(Object::toString).toArray());
    }

}
