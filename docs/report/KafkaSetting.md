# Kafka & Kafka UI Docker Compose 설정 문서

## 1. 개요

이 문서는 단일 브로커 Kafka 클러스터와 Kafka UI를 Docker Compose로 실행하기 위한 설정을 설명합니다.  
구성은 KRaft 모드(Zookeeper 없는 모드)를 기반으로 하며, 데이터 영속성을 위해 도커 볼륨을 사용합니다.

---

## 2. docker-compose.yml 설정

```yaml
version: "3.8"

services:
  kafka:
    image: public.ecr.aws/bitnami/kafka:3.9.1
    container_name: kafka
    ports:
      - "9094:9094"   # 외부 접속 포트
    volumes:
      - kafka-data:/bitnami/kafka
    environment:
      # KRaft 모드 (Zookeeper 없는 구성)
      KAFKA_CFG_NODE_ID: 0
      KAFKA_CFG_PROCESS_ROLES: controller,broker
      KAFKA_CFG_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_CFG_CONTROLLER_QUORUM_VOTERS: 0@127.0.0.1:9093

      # 리스너 설정
      KAFKA_CFG_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094
      KAFKA_CFG_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094
      KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT

      # 브로커 간 통신 리스너 지정 (단일 브로커라도 명시 권장)
      KAFKA_CFG_INTER_BROKER_LISTENER_NAME: PLAINTEXT

      # 필수 토픽(replication factor) 관련 설정 (단일 브로커 환경이라 1로 설정)
      KAFKA_CFG_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_CFG_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_CFG_TRANSACTION_STATE_LOG_MIN_ISR: 1

  kafka-ui:
    image: provectuslabs/kafka-ui:0.7.0
    container_name: kafka-ui
    ports:
       - "8081:8080"   # 웹 UI 접속 포트
    environment:
      - KAFKA_CLUSTERS_0_NAME=dev-kafka
      - KAFKA_CLUSTERS_0_BOOTSTRAP_SERVERS=kafka:9092
    depends_on:
      - kafka
    restart: always

volumes:
  kafka-data:
```

---

## 3. Kafka 서비스 설정

### 기본 정보
- **image**: Bitnami Kafka 3.9.1 버전 사용
- **container_name**: 컨테이너 이름을 `kafka`로 지정
- **ports**: 외부 접속을 위해 `9094` 포트를 열어둠
- **volumes**: 도커 볼륨 `kafka-data`를 `/bitnami/kafka` 경로에 마운트해 데이터 영속성 보장

### KRaft 모드 (Zookeeper 없는 모드)
- `KAFKA_CFG_NODE_ID=0` → 브로커/컨트롤러 ID
- `KAFKA_CFG_PROCESS_ROLES=controller,broker` → 단일 프로세스에서 컨트롤러와 브로커 역할 수행
- `KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER` → 컨트롤러 통신 리스너 이름
- `KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@127.0.0.1:9093` → 컨트롤러 투표자 지정 (단일 노드)

### 리스너 설정
- `KAFKA_CFG_LISTENERS`: Kafka가 바인딩할 리스너 정의
    - `PLAINTEXT://:9092` → 내부 브로커 통신
    - `CONTROLLER://:9093` → 컨트롤러 전용 통신
    - `EXTERNAL://:9094` → 외부 클라이언트 접속
- `KAFKA_CFG_ADVERTISED_LISTENERS`: 클라이언트가 접속 시 사용할 주소 지정
    - `PLAINTEXT://kafka:9092` → 도커 네트워크 내부 접근
    - `EXTERNAL://localhost:9094` → 호스트 머신 접근
- `KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP`: 모든 리스너를 PLAINTEXT로 설정

### 브로커 간 통신 리스너
- `KAFKA_CFG_INTER_BROKER_LISTENER_NAME=PLAINTEXT`  
  → 브로커 간 통신 리스너를 명시 (단일 브로커 환경에서도 설정 권장)

### 복제 관련 설정
- `KAFKA_CFG_OFFSETS_TOPIC_REPLICATION_FACTOR=1`
- `KAFKA_CFG_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1`
- `KAFKA_CFG_TRANSACTION_STATE_LOG_MIN_ISR=1`  
  → 단일 브로커 환경에서는 모든 replication factor를 1로 설정해야 정상 작동

---

## 4. Kafka UI 서비스 설정

- **image**: `provectuslabs/kafka-ui:0.7.0` → 안정적인 최신 버전
- **container_name**: `kafka-ui`
- **ports**: `8081` → 웹 UI 접속 포트 (http://localhost:8081)
- **environment**:
    - `KAFKA_CLUSTERS_0_NAME=dev-kafka` → UI에 표시될 클러스터 이름
    - `KAFKA_CLUSTERS_0_BOOTSTRAP_SERVERS=kafka:9092` → Kafka 브로커 주소
- **depends_on**: Kafka가 먼저 실행된 후 UI가 시작되도록 보장
- **restart**: 컨테이너 비정상 종료 시 자동 재시작

---

## 5. Volumes

```yaml
volumes:
  kafka-data:
```

- Kafka 데이터를 저장할 도커 볼륨 정의
- 컨테이너가 삭제되더라도 메시지 및 로그 데이터 유지 가능

---

## 6. 실행 및 접속 방법

### 컨테이너 실행
```bash
docker-compose up -d
```

### Kafka 브로커 접속
- 내부 네트워크: `kafka:9092`
- 외부 로컬 PC: `localhost:9094`

### Kafka UI 접속
- 브라우저에서 접속 → [http://localhost:8081](http://localhost:8081)

---

## 7. 요약

- 단일 브로커 Kafka 클러스터 (KRaft 모드)
- 데이터 영속성을 위한 볼륨 사용
- 내부/외부 리스너 및 복제 설정 최적화
- Kafka UI(0.7.0)로 토픽 및 메시지 관리 가능  
