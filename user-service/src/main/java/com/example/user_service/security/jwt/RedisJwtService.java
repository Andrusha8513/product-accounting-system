package com.example.user_service.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisJwtService {

    private final RedisTemplate<String , Object> redisTemplate;

    private static final String TOKEN_BLACK_LIST = "blk:token:";

    private static final String USER_BLOCKLIST_PREFIX ="blk:user:";

    private static final String CONFIRMATION_CODE_PREFIX="blk:email_code:";

    private static final String RESET_PASSWORD_CODE = "blk:res_pass_code:";


    public void saveTokenToBlackList(String token , long ttlMillis){
        if(ttlMillis > 0){
            redisTemplate.opsForValue().set(
                    TOKEN_BLACK_LIST + token,
                    "true",
                    ttlMillis,
                    TimeUnit.MILLISECONDS
            );
        }
    }


    public void  blockUserId(Long userId){
        redisTemplate.opsForValue().set(
                USER_BLOCKLIST_PREFIX + userId,
                "banned",
                20,
                TimeUnit.MINUTES
        );
    }



    public boolean isTokenBlacklisted(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_BLACK_LIST + token));
    }

//короче написла бан юзеров , но пока не решил как там все реализовать буду. Есть мысли  как улучшить  то что есть сейчас , но пока трогать не буду
    public boolean isUserBlocked(Long userId){
        return Boolean.TRUE.equals(redisTemplate.hasKey(USER_BLOCKLIST_PREFIX + userId));
    }

    //для супер быстрого разбана крч
    public void unblockUserId(Long userId){
        redisTemplate.delete(USER_BLOCKLIST_PREFIX + userId);
    }


    public void saveEmailConfirmation(String code){
        redisTemplate.opsForValue().set(
                CONFIRMATION_CODE_PREFIX + code,
                "active",
                15,
                TimeUnit.MINUTES
        );
    }

    public boolean isCodeAlive(String code){
        return Boolean.TRUE.equals(redisTemplate.hasKey(CONFIRMATION_CODE_PREFIX + code));
    }

    public void deleteConfirmationCode(String code){
        redisTemplate.delete(CONFIRMATION_CODE_PREFIX + code);
    }

    public void saveResetCode(String code){
        redisTemplate.opsForValue().set(
                RESET_PASSWORD_CODE + code,
                "active",
                15,
                TimeUnit.MINUTES
        );
    }

    public boolean isResetCodeAlive(String code){
        return Boolean.TRUE.equals(redisTemplate.hasKey(RESET_PASSWORD_CODE + code));
    }

    public void deleteResetCode(String code){
        redisTemplate.delete(RESET_PASSWORD_CODE + code);
    }
}



