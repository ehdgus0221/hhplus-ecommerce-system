package kr.hhplus.be.server.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort)
                .setConnectionPoolSize(20)
                .setConnectionMinimumIdleSize(5)
                .setConnectTimeout(10000)        // 10초
                .setTimeout(15000)               // 명령 실행 타임아웃 15초
                .setIdleConnectionTimeout(20000);// 유휴 커넥션 유지 시간 20초
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
