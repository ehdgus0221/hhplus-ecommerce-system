# 부하 테스트 보고서

##  배경

이커머스 시스템의 핵심 기능이 안정적으로 동작하도록 보장하기 위해 부하 테스트를 수행한다.

이번 테스트는 시스템의 한계치를 확인하고, 실제 트래픽 집중 상황에서 병목이나 장애 가능성을 사전에 파악하는 데 목적이 있다.

또한 테스트 결과를 기반으로 서버 용량 및 리소스 배치 계획을 검증할 수 있으며, 운영 환경에서 예상되는 트래픽에 맞춰 시스템 확장성을 점검할 수 있다.

## 대상 선정

테스트 대상은 트래픽이 순간적으로 집중될 가능성이 높은 핵심 비즈니스 기능으로 한정하였다.
이번 테스트에서는 선착순 쿠폰 발급 기능을 중심으로 진행한다.


### 선착순 쿠폰 발급

선착순 쿠폰 발급은 단시간에 많은 사용자가 동시에 접근하는 이벤트성 트래픽이 발생하는 기능이다.
따라서 Peak Test 방식을 적용하여, 순간 부하가 최고조에 달하는 구간에서도 시스템이 안정적으로 동작하는지 검증한다.

특정 시점에 다수의 사용자가 동시에 쿠폰을 요청하는 상황을 시뮬레이션하여,
트래픽이 집중될 때의 응답 시간, 처리량, 에러 발생 여부 등을 집중적으로 검증한다.
이를 통해 트래픽 급증 상황에서도 시스템이 안정적으로 동작하는지 확인할 수 있다.

## 테스트 환경 구축

테스트 환경은 로컬 PC의 도커 컨테이너를 활용하여 구성하였다.

### Spring 테스트 환경

다음은 스플링 어플리케이션 `docker-compose.yml` 일부이다.

```yaml
version: '3'
services:
  api:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - TZ="Asia/Seoul"
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 4G
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml:ro
```
Spring 애플리케이션의 리소스를 제한하기 위해 CPU는 2 vCPU, 메모리는 4 GB로 설정하였다.  
추가로, Spring Actuator를 통해 메트릭을 수집하고, Prometheus 및 Grafana와 연동하여 모니터링 대시보드를 구성하였다.
#### `build.gradle.kts`의 의존성 추가

```groovy
dependencies {
    // ... 생략 ...
    // Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
}
```

#### `prometheus.yml` 추가

```yaml
global:
  scrape_interval: 15s
scrape_configs:
  - job_name: 'spring-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: [ 'api:8080' ]
```

#### `application.yml`의 actuator 설정 추가

```yaml
management:
  endpoint:
    health:
      show-details: always
  endpoints:
    web:
      exposure:
        include: "*"
  metrics:
    enable:
      all: true
```

---

### K6 테스트 환경

부하 테스트 도구로는 K6를 사용하였으며, InfluxDB 및 Grafana와 연동하여 테스트 결과를 시각화하였다.  
다음은 InfluxDB와 Grafana를 위한 `docker-compose.yml` 일부이다.

```yaml
services:
  influxdb:
    image: influxdb:1.8
    networks:
      - k6
      - grafana
    ports:
      - "8086:8086"
    environment:
      - INFLUXDB_DB=k6
  grafana:
    image: grafana/grafana:9.3.8
    networks:
      - grafana
    ports:
      - "3000:3000"
    environment:
      - GF_AUTH_ANONYMOUS_ORG_ROLE=Admin
      - GF_AUTH_ANONYMOUS_ENABLED=true
      - GF_AUTH_BASIC_ENABLED=false
    volumes:
      - ./grafana:/etc/grafana/provisioning/
```

이와 같이 로컬 Docker 기반의 통합 테스트 및 모니터링 환경을 구축하였다.



## 테스트 설계 및 목적

### 선착순 쿠폰 발급

#### 테스트 시나리오

선착순 쿠폰 발급은 이벤트성 트래픽이 단시간에 많이 집중되는 비지니스로,  
급격한 부하가 집중되는 Peak Test 방식으로 최대 1000 VU까지 테스트를 진행한다.

1. 30초 동안 100명까지 ramp-up
2. 1분 동안 500명 유지
3. 2분 동안 1000명 유지 (피크 구간)
4. 1분 동안 500명으로 감소
5. 30초 동안 종료

#### 목표 TPS

| 기능    | 사용자 기준 | 처리 빈도       | 목표 TPS  |
|-------|--------|-------------|---------|
| 쿠폰 발급 | 100명   | 0.5초당 1건 처리 | 200 TPS |

#### 테스트 데이터

테스트를 위해 쿠폰 데이터를 생성하였다.

```sql
CREATE PROCEDURE generate_coupon_data()
BEGIN
  DECLARE
i INT DEFAULT 1;

INSERT INTO coupon (name, quantity, discount_rate, expired_at, status)
VALUES (CONCAT('쿠폰명', i), 10000, 0.3, DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), 'PUBLISHABLE');

END
//
```

쿠폰 발급을 위한 쿠폰 발급 여부를 확인하는 값을 Redis에 적재 하였다.

```redis
SET coupon_avaliable:{couponId} true
```

#### 테스트 스크립트

##### SLA 설정

- **HTTP 요청의 P99 응답 시간**은 **1초 이하**를 목표로 한다.
- **HTTP 요청 실패율**은 **5% 미만**으로 제한한다.

쿠폰 ID는 고정하였으며, 사용자 ID는 최대한 중복 ID가 생기지 않게 끔 VU ID와 시나리오 반복 횟수를 조합하여 생성하였다.

```javascript
import http from 'k6/http';
import {sleep, check, group} from "k6";
import {randomIntBetween} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import exec from 'k6/execution';

export const options = {
    stages: [
        { duration: '30s', target: 100 },   // 30초 동안 100명까지 ramp-up
        { duration: '1m', target: 500 },    // 1분 동안 500명 유지
        { duration: '2m', target: 1000 },   // 2분 동안 1000명 유지 (피크 구간)
        { duration: '1m', target: 500 },    // 1분 동안 500명으로 감소
        { duration: '30s', target: 0 },     // 30초 동안 종료
    ],
    thresholds: {
        http_req_duration: ['p(99)<1000'],
        http_req_failed: ['rate<0.05']
    },
};

const BASE_URL = 'http://127.0.0.1:8080/api/v0';

export default function main() {
    const userId = (exec.vu.idInTest * 1_000_000) + exec.vu.iterationInScenario;
    const couponId = 1;

    // 쿠폰 발급 요청
    group('쿠폰발급', () => {
        const payload = JSON.stringify({
            couponId: couponId
        });

        const params = {
            headers: {
                'Content-Type': 'application/json',
            },
            tags: {name: '쿠폰발급'}
        };

        const response = http.post(
            `${BASE_URL}/users/${userId}/coupons/publish`,
            payload,
            params
        );

        check(response, {
            '쿠폰 발급 성공': (r) => r.status === 200,
        });
    });

    sleep(randomIntBetween(1, 3));
}
```