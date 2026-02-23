package com.example.profile_service.redis;

import com.example.profile_service.dto.ProfileSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ProfileRedisRepository {

    private final RedisTemplate<String , Object> redisTemplate;
    private static final String KEY_PREFIX = "profile:summary:";
    private static final long TTL_MINUTES = 15;

    public void saveProfileSummery(Long profileId , ProfileSummaryDto summary){
        String key = KEY_PREFIX + profileId;
        redisTemplate.opsForValue().set(key , summary , TTL_MINUTES , TimeUnit.MINUTES);
    }

    public Optional<ProfileSummaryDto> getProfileSummery(Long profileId){
        String key = KEY_PREFIX + profileId;
        ProfileSummaryDto summary = (ProfileSummaryDto) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(summary);
    }

    public void deleteProfileSummery(Long profileId){
        String key = KEY_PREFIX + profileId;
        redisTemplate.delete(key);
    }

}
