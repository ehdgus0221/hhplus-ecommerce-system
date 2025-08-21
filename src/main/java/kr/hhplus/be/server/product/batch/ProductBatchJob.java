package kr.hhplus.be.server.product.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductBatchJob {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    @Scheduled(cron = "0 30 0 * * *") // 매일 00:30
    public void aggregatePopularProducts() {
        LocalDate today = LocalDate.now();

        String key1 = "sales:" + today.minusDays(1).format(FORMATTER);
        String key2 = "sales:" + today.minusDays(2).format(FORMATTER);
        String key3 = "sales:" + today.minusDays(3).format(FORMATTER);

        String unionKey = "sales:last3days";

        // ZUNIONSTORE 수행
        redisTemplate.opsForZSet().unionAndStore(key1, Arrays.asList(key2, key3), unionKey);

        // TTL 1일 설정 (내일 새로 갱신되므로)
        redisTemplate.expire(unionKey, Duration.ofDays(1));

        // 상위 5개 조회
        Set<ZSetOperations.TypedTuple<Object>> topProducts =
                redisTemplate.opsForZSet().reverseRangeWithScores(unionKey, 0, 4);

        if (topProducts != null) {
            log.info("최근 3일 인기상품 TOP5");
            for (ZSetOperations.TypedTuple<Object> product : topProducts) {
                log.info("상품ID={}, 판매수량={}", product.getValue(), product.getScore());
            }
        }
    }
}
