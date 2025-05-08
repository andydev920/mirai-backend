package jp.mirai.framework.idempotent.config;

import jp.mirai.framework.idempotent.core.aop.IdempotentAspect;
import jp.mirai.framework.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import jp.mirai.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import jp.mirai.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import jp.mirai.framework.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;
import jp.mirai.framework.idempotent.core.redis.IdempotentRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import jp.mirai.framework.redis.config.MiraiRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@AutoConfiguration(after = MiraiRedisAutoConfiguration.class)
public class MiraiIdempotentConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisDAO(stringRedisTemplate);
    }

    // ========== 各种 IdempotentKeyResolver Bean ==========

    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        return new DefaultIdempotentKeyResolver();
    }

    @Bean
    public UserIdempotentKeyResolver userIdempotentKeyResolver() {
        return new UserIdempotentKeyResolver();
    }

    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        return new ExpressionIdempotentKeyResolver();
    }

}
