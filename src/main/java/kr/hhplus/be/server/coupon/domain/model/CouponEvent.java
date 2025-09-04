package kr.hhplus.be.server.coupon.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CouponEvent {

    @Getter
    @Builder
    public static class Issued {
        private final Long userId;
        private final Long couponId;
        private final LocalDateTime issuedAt;

        public static Issued of(Long userId, Long couponId) {
            return Issued.builder()
                    .userId(userId)
                    .couponId(couponId)
                    .issuedAt(LocalDateTime.now())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Used {
        private final Long userCouponId;
        private final LocalDateTime usedAt;

        public static Used of(Long userCouponId) {
            return Used.builder()
                    .userCouponId(userCouponId)
                    .usedAt(LocalDateTime.now())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Expired {
        private final Long couponId;
        private final LocalDateTime expiredAt;

        public static Expired of(Long couponId) {
            return Expired.builder()
                    .couponId(couponId)
                    .expiredAt(LocalDateTime.now())
                    .build();
        }
    }
}