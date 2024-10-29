package ssafy.horong.domain.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static ssafy.horong.common.constant.redis.CONST_VALUE.EXPIRED;
import static ssafy.horong.common.constant.redis.KEY_PREFIX.BLACKLIST;

@Component
@Slf4j
public class BlacklistTokenRedisRepository extends BaseRedisRepository<String> {
    public BlacklistTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.prefix = BLACKLIST;
        this.ttl = 60 * 60 * 24 * 7L;
    }

    public void save(String token, Long ttl) {
        super.save(token, EXPIRED, ttl);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(generateKeyFromId(key));
    }
}
