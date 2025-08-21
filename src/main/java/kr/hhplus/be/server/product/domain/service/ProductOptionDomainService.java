package kr.hhplus.be.server.product.domain.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class ProductOptionDomainService {
    private final ProductOptionRepository productOptionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    public void decreaseStock(Long productOptionId, int stock) {
        ProductOption option = productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new EntityNotFoundException("상품 옵션이 존재하지 않습니다."));
        option.decreaseStock(stock); // 도메인 로직
    }

    public void recordSale(Long productId, int quantity) {
        String todayKey = "sales:" + LocalDate.now().format(FORMATTER);
        redisTemplate.opsForZSet().incrementScore(todayKey, productId.toString(), quantity);
        redisTemplate.expire(todayKey, Duration.ofDays(4)); // 날짜로 key를 잡아 TTL 설정해주기 (3일 + 버퍼)
    }
}
