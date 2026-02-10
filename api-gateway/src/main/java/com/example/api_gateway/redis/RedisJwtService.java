package com.example.api_gateway.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisJwtService {

    private final RedisTemplate<String , Object> redisTemplate;

    private static final String TOKEN_BLACKLIST = "blk:token:";

    private static final String USER_BLACKLIST ="blk:user:";




    public void saveTokenToBlackList(String token , long ttlMillis){
        if(ttlMillis > 0){
            redisTemplate.opsForValue().set(
                    TOKEN_BLACKLIST + token,
                    "true",
                    ttlMillis,
                    TimeUnit.MILLISECONDS
            );
        }
    }


    public void  blockUserId(Long userId){
        redisTemplate.opsForValue().set(
                USER_BLACKLIST + userId,
                "banned",
                20,
                TimeUnit.MINUTES
        );
    }



    public boolean isTokenBlacklisted(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_BLACKLIST + token));
    }

//короче написла бан юзеров , но пока не решил как там все реализовать буду. Есть мысли  как улучшить  то что есть сейчас , но пока трогать не буду
    public boolean isUserBlocked(Long userId){
        return Boolean.TRUE.equals(redisTemplate.hasKey(USER_BLACKLIST + userId));
    }

    //для супер быстрого разбана крч
    public void unblockUserId(Long userId){
        redisTemplate.delete(USER_BLACKLIST + userId);
    }
}
