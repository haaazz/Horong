package ssafy.sera.domain.redis;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public abstract class BaseRedisRepository<T> {
    protected RedisTemplate<String, T> redisTemplate;
    protected String prefix;
    protected Long ttl;

    public void save(String id, T data) {
        redisTemplate.opsForValue().set(generateKeyFromId(id), data, ttl, TimeUnit.SECONDS);
    }

    public void save(String id, T data, Long ttl) {
        redisTemplate.opsForValue().set(generateKeyFromId(id), data, ttl, TimeUnit.SECONDS);
    }

    public Optional<T> findById(String id) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(generateKeyFromId(id)));
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    public Boolean delete(String id) {
        return redisTemplate.delete(generateKeyFromId(id));
    }

    protected String generateKeyFromId(String id) {
        return prefix + id;
    }

}
