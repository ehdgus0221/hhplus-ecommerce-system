package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.application.CouponService;
import kr.hhplus.be.server.coupon.domain.model.Coupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CouponServiceConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    private void executeConcurrency(List<Runnable> runnables) {
        ExecutorService executorService = Executors.newFixedThreadPool(runnables.size());

        List<CompletableFuture<Void>> futures = runnables.stream()
                .map(runnable -> CompletableFuture.runAsync(runnable, executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }


    @Test
    @DisplayName("선착순 쿠폰 동시 발급 시, 재고 수에 맞게 발급해야 한다.")
    void issueCouponWithPessimisticWriteLock() {
        // given
        Coupon coupon = Coupon.create("1번 쿠폰", 20, 10, 5, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<Runnable> tasks = IntStream.range(0, 20)
                .mapToObj(i -> (Runnable) () -> {
                    Long userId = (long) i; // 각 스레드마다 다른 userId 부여
                    CouponIssueRequestDto request = new CouponIssueRequestDto(userId, coupon.getId());
                    try {
                        couponService.issue(request);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                })
                .toList();

        // when
        executeConcurrency(tasks);

        // then
        // 20명의 다른 사용자가 5개의 선착순 쿠폰을 발급 요청 -> 5명 성공 / 15명 실패해야함
        assertThat(successCount.get()).isEqualTo(5);
        assertThat(failCount.get()).isEqualTo(15);
    }

}
