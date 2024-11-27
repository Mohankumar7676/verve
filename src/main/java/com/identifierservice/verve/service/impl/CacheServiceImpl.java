package com.identifierservice.verve.service.impl;
import static com.identifierservice.verve.constants.ApplicationConstants.UNIQUE_IDENTIFIERS_KEY;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.identifierservice.verve.service.CacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean addUniqueIdentifiers(Integer identifier) {
        if (redisTemplate.opsForSet().add(UNIQUE_IDENTIFIERS_KEY, identifier.toString()) != null) {
            redisTemplate.expire(UNIQUE_IDENTIFIERS_KEY, 1, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    @Override
    public long getUniqueIdentifiersCount() {
        Long size = redisTemplate.opsForSet().size(UNIQUE_IDENTIFIERS_KEY);
        return size != null ? size : 0L;
    }

}
