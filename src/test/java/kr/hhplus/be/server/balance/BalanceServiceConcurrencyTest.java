package kr.hhplus.be.server.balance;

import kr.hhplus.be.server.balance.application.BalanceService;
import kr.hhplus.be.server.balance.domain.model.Balance;
import kr.hhplus.be.server.balance.domain.repository.BalanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BalanceServiceConcurrencyTest {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;

    private void executeConcurrency(int threadCount, Runnable runnable) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
                .mapToObj(i -> CompletableFuture.runAsync(runnable, executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }

    private void executeConcurrency(List<Runnable> runnables) {
        ExecutorService executorService = Executors.newFixedThreadPool(runnables.size());

        List<CompletableFuture<Void>> futures = runnables.stream()
                .map(runnable -> CompletableFuture.runAsync(runnable, executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }

    @DisplayName("잔액 충전 동시 요청 시 단일 요청만 정상 처리되는지 확인한다.")
    @Test
    void chargeBalanceWithOptimisticLock() {
        // given
        Long userId = 1L;
        Balance balance = Balance.createInitial(userId);
        balanceRepository.save(balance);

        long chargeAmount = 1_000L;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(3, () -> {
            try {
                balanceService.charge(userId, chargeAmount);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(2);
        assertThat(successCount.get() + failCount.get()).isEqualTo(3);

        Optional<Balance> updated = balanceRepository.findByUserId(userId);
        assertThat(updated).isPresent();

        Balance balanceEntity = updated.get();
        assertThat(balanceEntity.getAmount()).isEqualTo(chargeAmount);
        assertThat(balanceEntity.getVersion()).isEqualTo(1L);
    }

    @DisplayName("잔액 사용 동시 요청 시 단일 요청만 정상 처리되는지 확인한다.")
    @Test
    void useBalanceWithOptimisticLock() {
        // given
        Long userId = 2L;
        Balance balance = Balance.createInitial(userId);
        balance.addAmount(1_000L);
        balanceRepository.save(balance);

        long useAmount = 500L;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(3, () -> {
            try {
                balanceService.use(userId, useAmount);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(2);
        assertThat(successCount.get() + failCount.get()).isEqualTo(3);

        Balance updated = balanceRepository.findByUserId(userId).orElseThrow();
        assertThat(updated.getAmount()).isEqualTo(500L);
        assertThat(updated.getVersion()).isEqualTo(1L);
    }

    @DisplayName("충전과 사용이 동시에 요청될 때, 단일 작업만 성공 처리되는지 검증한다.")
    @Test
    void chargeAndUseBalanceWithOptimisticLock() {
        // given
        Long userId = 3L;
        Balance balance = Balance.createInitial(userId);
        balance.addAmount(1_000L);
        balanceRepository.save(balance);

        long chargeAmount = 500L;
        long useAmount = 300L;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(List.of(
                () -> {
                    try {
                        balanceService.charge(userId, chargeAmount);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                },
                () -> {
                    try {
                        balanceService.use(userId, useAmount);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                }
        ));

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(successCount.get() + failCount.get()).isEqualTo(2);

        Balance updated = balanceRepository.findByUserId(userId).orElseThrow();

        // 잔액은 충전 or 사용 중 하나만 성공하므로, 가능한 최종 잔액 조건 체크
        // 초기 1000 + 500(충전) - 300(사용) 이므로 1500, 700 중 하나일 것
        assertThat(updated.getAmount()).isIn(700L, 1500L);
        assertThat(updated.getVersion()).isEqualTo(1L);
    }


}
