package com.example.api_gateway.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisJwtService {

    private final RedisTemplate<String , Object> redisTemplate;

    private static final String token_black_list = "blk:token:";

    private static final String user_blocklist_prefix="blk:user:";




    public void saveTokenToBlackList(String token , long ttlMillis){
        if(ttlMillis > 0){
            redisTemplate.opsForValue().set(
                    token_black_list + token,
                    "true",
                    ttlMillis,
                    TimeUnit.MILLISECONDS
            );
        }
    }


    public void  blockUserId(Long userId){
        redisTemplate.opsForValue().set(
                user_blocklist_prefix + userId,
                "banned",
                20,
                TimeUnit.MINUTES
        );
    }



    public boolean isTokenBlacklisted(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(token_black_list + token));
    }

//короче написла бан юзеров , но пока не решил как там все реализовать буду. Есть мысли  как улучшить  то что есть сейчас , но пока трогать не буду
    public boolean isUserBlocked(Long userId){
        return Boolean.TRUE.equals(redisTemplate.hasKey(user_blocklist_prefix + userId));
    }

    //для супер быстрого разбана крч
    public void unblockUserId(Long userId){
        redisTemplate.delete(user_blocklist_prefix + userId);
    }
}
