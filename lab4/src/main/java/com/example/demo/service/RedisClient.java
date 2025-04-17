
package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisClient {

    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> valueOps;

    @Autowired
    public RedisClient(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }

    public void set(String key, String value) {
        valueOps.set(key, value);
    }

    public void set(String key, String value, Duration timeout) {
        valueOps.set(key, value, timeout);
    }

    public String get(String key) {
        return valueOps.get(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Boolean setIfAbsent(String key, String value, Duration timeout) {
        return valueOps.setIfAbsent(key, value, timeout);
    }

    public Long increment(String key, Duration ttl) {
        Long count = valueOps.increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, ttl); // set expiry only if first call
        }
        return count;
    }
}
