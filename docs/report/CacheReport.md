# Redis 캐싱 전략 설계 (상품 조회)

## 1. 분석 배경

* 이커머스 시스템에서는 **전체 상품 조회**와 같은 반복 조회 쿼리가 대량 트래픽 시 DB 과부하를 유발할 수 있음.
* 인기 상품, 메인 페이지 상품 목록 등 **조회 빈도가 높은 데이터**는 DB 지연(Latency) 문제가 발생할 가능성이 높음.
* 따라서 **읽기 최적화**와 **DB 부하 완화**를 위해 Redis 캐싱을 도입함.

## 2. 적용 대상

* 전체 상품 조회 API

```java
@GetMapping
public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
    List<ProductResponseDto> products = productService.getAllProducts();
    return ResponseEntity.ok(products);
}
```

* 조회 대상: 판매중인 상품 (`ProductStatus.ON_SALE`)
* 조회 결과는 자주 변경되지 않고, **읽기 빈도가 높음** → 캐싱 대상 적합

## 3. 캐싱 구현

* 캐시 매니저: `RedisCacheManager`
* 캐시 키: `"products::all"`
* TTL(Time-To-Live): 1일 (`Duration.ofDays(1L)`)

    * 도입 이유: 상품 데이터는 자주 변경되지 않으므로 장기 캐싱으로 DB 호출 최소화
* 직렬화 전략

    * Key: `StringRedisSerializer`
    * Value: `GenericJackson2JsonRedisSerializer`
* Spring Cache 적용

```java
@Cacheable(value = "products", key = "'all'", cacheManager = "cacheManager")
@Transactional(readOnly = true)
public List<ProductResponseDto> getAllProducts() {
    return productDomainService.getAllActiveProducts().stream()
            .map(ProductResponseDto::new)
            .collect(Collectors.toList());
}
```

* 구현 설명

    * `@Cacheable` → 캐시에서 조회 실패 시만 DB 접근, 읽기 성능 최적화
    * `key = "'all'"` → 전체 상품 조회를 대표하는 단일 키 사용, 캐시 관리 용이
    * `readOnly = true` → 트랜잭션 최적화, DB 세션 성능 향상
    * 캐시 무효화는 상품 추가/수정/삭제 시 수행 → 데이터 신선도 확보 (현재 프로젝트에서는 구현하지 않은 상태로 나중에 상품 추가/수정/삭제가 있는 경우 수행하기)

## 4. Redis 설정 예시

```java
@EnableCaching
@Configuration
public class RedisCacheConfig {
    @Bean
    LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofDays(1L)); // TTL 1일

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(factory)
                .cacheDefaults(cacheConfig)
                .build();
    }
}
```

## 5. TTL 설정 관련

* 전체 상품 조회처럼 자주 변경되지 않는 데이터는 대체적으로 TTL을 **1\~7일** 범위에서 설정한다고 한다.
* 실무 기준:

    * 1일: 최신 상품 반영과 캐시 유지 균형
    * 7일: 변경이 적은 데이터, 조회 성능 극대화
* 상품 추가/수정/삭제 시 캐시 무효화 → TTL과 무관하게 데이터 일관성 유지

## 6. 기대 효과

* 반복 조회 시 DB 호출 횟수 감소 → 트래픽 급증에도 지연 최소화
* 메인 페이지 상품 조회 속도 향상
* TTL 기반 장기 캐싱으로 데이터 조회 효율 극대화
* 무효화 전략과 결합 → 데이터 신선도 확보

## 7. 캐시 무효화 전략

* 상품 추가/수정/삭제 시 `"products::all"` 캐시 삭제 (상품 추가/수정/삭제 기능 도입 시 구현 필요)
* Redis를 통한 분산 환경에서도 일관성 유지 가능

## 8. 결론

* Redis 캐싱 적용으로 전체 상품 조회 성능 안정화
* TTL 1일 설정과 무효화 전략 병행 → 성능과 데이터 신선도 균형 확보
 