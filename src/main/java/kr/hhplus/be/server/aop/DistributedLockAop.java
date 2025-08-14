package kr.hhplus.be.server.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @DistributedLock 선언 시 수행되는 Aop class
 * Redis 기반 분산 락 획득 및 해제 처리
 */
@Aspect
@Component
@RequiredArgsConstructor
@Order(1)  // Lock 획득 > transaction 시작 > Service 로직 > transaction 종료 > Lock 반납
@Slf4j
public class DistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;

    @Around("@annotation(kr.hhplus.be.server.aop.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {

        // 메소드 정보 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 메소드에 선언된 @DistributedLock 가져오기
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // SpEL 기반 동적 키 평가
        String keyExpression = distributedLock.key();
        String key = evaluateSpEL(keyExpression, joinPoint);

        // Redisson RLock 생성
        RLock rLock = redissonClient.getLock(REDISSON_LOCK_PREFIX + key);

        try {
            boolean acquired;

            // failFast 옵션 확인
            // - true: 락을 즉시 시도, 실패 시 예외 발생
            // - false: waitTime 동안 락 획득 대기 후 실패 시 예외 발생
            if (distributedLock.failFast()) {
                acquired = rLock.tryLock(0, distributedLock.leaseTime(), distributedLock.timeUnit());
            } else {
                acquired = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            }

            // 락 획득 실패 처리
            if (!acquired) {
                log.warn("분산 락 획득 실패: key={}", key);
                throw new LockAcquisitionException("분산 락 획득 실패: key=" + key);
            }

            // 락 획득 성공 시 메소드 실행
            log.info("분산 락 획득 성공: key={}", key);
            return joinPoint.proceed();

        } catch (InterruptedException e) {
            // 락 획득 중 스레드 인터럽트 발생
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
            log.error("분산 락 획득 중 인터럽트 발생: key={}", key, e);
            throw e;
        } finally {
            // 락 해제 처리
            try {
                if (rLock.isHeldByCurrentThread()) {
                    rLock.unlock();
                    log.info("분산 락 해제 완료: key={}", key);
                }
            } catch (IllegalMonitorStateException e) {
                // 이미 다른 스레드/코드에서 해제된 경우 무시
                log.warn("이미 해제된 락에 대한 unlock 시도: key={}", key);
            }
        }
    }

    /**
     * SpEL 표현식 평가
     * - 메소드 파라미터를 변수로 사용 가능
     * 예: @DistributedLock(key = "#userId + '-' + #productId")
     */
    private String evaluateSpEL(String expression, ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(expression).getValue(context, String.class);
    }
}