# Redis 기반 선착순 쿠폰 발급 시스템 설계 보고서

## 1. 배경

기존 시스템은 **RDBMS 기반 트랜잭션**으로 쿠폰 발급을 처리했습니다. 그러나 이벤트성 쿠폰, 인기 쿠폰과 같이 **동시 접속자가 많은 상황**에서는 다음과 같은 문제점의 가능성이 높았습니다.

- 동시에 여러 사용자가 요청 시 **재고 초과 발급 가능성**
- 트랜잭션 지연으로 인해 **응답 지연 및 DB 부하 증가**
- 실시간 순위/대기열 기능 구현의 어려움

이를 개선하기 위해, **Redis 기반 선착순 쿠폰 발급 로직**으로 전환했습니다. 
Redis의 **빠른 메모리 접근 속도와 다양한 자료구조**를 활용하여 실시간 처리 및 동시성 제어를 강화했습니다.

---

---

## 2. 시스템 동작 시나리오

### 2.1 사용자 요청 단계

1. 사용자가 쿠폰 발급 요청을 수행
2. Redis Sorted Set에 후보자로 등록

   ```text
   ZADD user_coupon:{couponId} NX {요청시각} {userId}
   ```

   * **NX 옵션**: 이미 등록된 경우 추가하지 않고 중복 요청 방지
   * 발급 요청 성공 시 `UserCouponResponseDto.pending()` 반환 (DB에는 아직 발급되지 않은 상태)

---

### 2.2 후보자 배치 발급 (1분 주기 스케줄러)

1. **발급 가능한 쿠폰 조회**

   ```java
   List<Coupon> activeCoupons = couponRepository.findByStatus(CouponStatus.START);
   ```
2. **발급 가능 수량 계산**

   ```java
   remaining = coupon.getQuantity() - userCouponRepository.countByCouponId(couponId);
   ```
3. **Redis 후보자 조회**

   ```java
   Set<Long> candidates = redisRepository.getCandidates(coupon.getId(), remaining);
   ```
4. **DB에 실제 쿠폰 발급**

   ```java
   List<UserCoupon> userCoupons = candidates.stream()
           .map(userId -> UserCoupon.issue(userId, coupon))
           .toList();
   userCouponRepository.saveAll(userCoupons);
   ```
5. **Redis 후보자 목록 제거**

   ```java
   redisRepository.removeCandidates(coupon.getId(), new ArrayList<>(candidates));
   ```
6. 발급 완료된 사용자는 DB 기준으로 실제 쿠폰 발급 상태로 업데이트

---

### 2.3 쿠폰 발급 종료 (3분 주기 스케줄러)

1. 발급 중인 쿠폰 목록 조회
2. 발급 완료 수량 확인
3. **모든 수량이 발급 완료되면 쿠폰 상태 FINISHED 변경**

   ```java
   coupon.finish();
   ```
4. 이후 해당 쿠폰은 추가 발급 불가

---

## 3. Redis 활용 설계

| 역할         | 설명                               |
| ---------- | -------------------------------- |
| Sorted Set | 요청 시각 순으로 후보자 정렬, 선착순 처리         |
| NX 옵션      | 중복 등록 방지, 동일 사용자가 여러 번 요청 불가     |
| 후보자 조회     | 남은 쿠폰 수량만큼 DB 발급 대상 선정           |
| 후보자 제거     | DB 발급 후 Redis 동기화, 중복 방지 및 상태 관리 |

---


## 4. 장점

* **실시간성**: 사용자 요청 즉시 Redis 후보자 등록
* **중복 방지**: NX 옵션 + DB 트랜잭션으로 동일 사용자 중복 발급 방지
* **고가용성**: Redis를 통한 분산 환경에서도 안정적 처리 가능
* **확장성**: 배치 주기 조정으로 시스템 부하 관리 가능
* **DB 부하 최소화**: Redis 후보자 → 배치 Insert 방식 적용


---

## 5. 한계점 및 개선 가능성

1. **실시간 확정 발급 불가**: 배치 주기(1분) 단위로 DB 반영 → 일부 요청 지연 발생 가능
2. **Redis 장애 시 후보자 누락 위험**: Redis 다운 시 후보자 데이터 유실 가능
3. **발급 종료 주기 단위 지연**: 3분 주기 스케줄러로 인해 쿠폰 종료 반영 지연
4. **대규모 트래픽 처리**: 후보자 수가 매우 많을 경우 Redis ZSET 연산 비용 증가
5. **분산 환경 동시성**: 추가적인 Redisson 분산락 사용 고려 가능

개선안:

* 발급 종료 실시간화 또는 이벤트 기반 처리

---

## 8. 결론

Redis 기반 후보자 관리와 배치 발급 구조를 적용함으로써 **실시간 선착순 쿠폰 발급**을 안정적으로 구현하였습니다.
후보자 등록 → DB 반영 → 발급 종료로 이어지는 **비동기적 고가용성 시스템** 설계를 통해 기존 RDBMS 기반 로직 대비 성능과 안정성을 확보했습니다.