package kr.hhplus.be.server.coupon.batch;


import kr.hhplus.be.server.coupon.application.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
@Slf4j
public class IssueCouponsBatchJob {

    private final CouponService couponService;

    // 1분마다 후보자 배치 발급
    @Scheduled(cron = "0 */1 * * * *")
    public void batchIssue() {
        couponService.batchIssueCoupons();
    }

    // 3분마다 쿠폰 발급 종료
    @Scheduled(cron = "0 */3 * * * *")
    public void finishCoupons() {
        couponService.finishCoupons();
    }
}
