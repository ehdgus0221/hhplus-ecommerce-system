package kr.hhplus.be.server.coupon.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponStatus {

    ISSUED("등록"),
    CANCELED("취소"),
    START("발급가능"),
    END("발급완료");

    private final String description;


}
