import http from 'k6/http';
import { sleep, check, group } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import exec from 'k6/execution';

// ==================== 테스트 옵션 ====================
// stages: 부하 프로파일 정의 (점진적 증가/감소 )
// thresholds: 성능 기준 (성공 조건)
export const options = {
    stages: [
        { duration: '30s', target: 100 },   // 30초 동안 100명까지 ramp-up
        { duration: '1m', target: 500 },    // 1분 동안 500명 유지
        { duration: '2m', target: 1000 },   // 2분 동안 1000명 유지 (피크 구간)
        { duration: '1m', target: 500 },    // 1분 동안 500명으로 감소
        { duration: '30s', target: 0 },     // 30초 동안 종료
    ],
    thresholds: {
        http_req_duration: ['p(99)<1000'],   // 전체 요청 중 99%가 1초 미만 응답
        http_req_failed: ['rate<0.05'],      // 실패율 < 5%
    },
};

// ==================== 기본 URL ====================
const BASE_URL = 'http://127.0.0.1:8080/api/v0';
const MAX_USERS = 10000; // 사용자 수

// ==================== 시나리오 ====================
export default function main() {
    const userId = ((exec.vu.idInTest + exec.vu.iterationInScenario) % MAX_USERS) + 1;
    const couponId = 1; // 부하 테스트 대상 쿠폰 ID

    // -------------------- 쿠폰 발급 요청 --------------------
    group('쿠폰발급', () => {
        const payload = JSON.stringify({
            couponId: couponId,
        });

        const params = {
            headers: { 'Content-Type': 'application/json' },
            tags: {
                name: '쿠폰발급',
                endpoint: '/users/:id/coupons/issue',
                method: 'POST',
            },
        };

        const response = http.post(
            `${BASE_URL}/users/${userId}/coupons/issue`,
            payload,
            params
        );

        // -------------------- 응답 검증 --------------------
        check(response, {
            // 상태 코드 검증
            '쿠폰 발급 성공': (r) => r.status === 200,

            // 응답 지연 검증 (500ms 이내면 통과)
            '요청 지연 < 500ms': (r) => r.timings.duration < 500,

            // 응답 바디 검증 (선택: 서버에서 "issued" 같은 키워드를 내려줄 때)
            '응답에 issued 포함': (r) => r.body && r.body.includes('issued'),
        });
    });

    // -------------------- 대기 시간 --------------------
    // 기존: 1~3초 랜덤 sleep → 그대로 유지 (현실적인 사용자 think time 반영)
    sleep(randomIntBetween(1, 3));
}
