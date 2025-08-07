package kr.hhplus.be.server.coupon.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCouponStatus {
    USED("사용완료"),
    UNUSED("미사용");

    private final String description;
}
