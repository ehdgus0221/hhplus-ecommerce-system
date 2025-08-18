package kr.hhplus.be.server.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;


/**
 * 운영 환경용 분산 락 애노테이션
 *
 * 옵션:
 * key: 락 키 (SpEL 지원)
 * timeUnit: waitTime, leaseTime 단위
 * waitTime: 락 획득 대기 시간
 * leaseTime: 락 임대 시간
 * failFast: 락 획득 실패 시 즉시 예외 발생
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLock {

    /**
     * 락의 이름
     */
    String key() default "";

    /**
     * 락의 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 락을 기다리는 시간 (default - 10s)
     * 락 획득을 위해 waitTime 만큼 대기한다
     */
    long waitTime() default 10L;

    /**
     * 락 임대 시간 (default - 15s)
     * 락을 획득한 이후 leaseTime 이 지나면 락을 해제한다
     */
    long leaseTime() default 15L;

    boolean failFast() default false; // 락 실패 시 바로 예외 발생

}