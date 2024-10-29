package ssafy.horong.domain.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static ssafy.horong.common.constant.redis.KEY_PREFIX.ACCESS_TOKEN;

@Component
@Slf4j
public class RefreshTokenRedisRepository extends BaseRedisRepository<String> {
    public RefreshTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.prefix = ACCESS_TOKEN;
        this.ttl = 60L * 60L * 24L * 30L; // 30 days
        this.redisTemplate = redisTemplate;
    }
}
