package org.example.postservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postservice.Model.UserCache;
import org.example.postservice.dto.UserDto;
import org.example.postservice.repository.UserCacheRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostKafkaConsumer {
    private final UserCacheRepository userCacheRepository;
        @KafkaListener(topics = "user",
                groupId = "post-service")
        public void consumerEmail(UserDto userDto) {
            log.info("Пришел юзер {}" ,userDto.getEmail());
            UserCache userCache = userCacheRepository.findByUserId(userDto.getId())
                            .orElse(new UserCache());

            userCache.setUserId(userDto.getId());
            userCache.setEmail(userDto.getEmail());
            userCache.setId(userDto.getId());
            userCache.setEmail(userDto.getEmail());
            userCache.setSecondName(userDto.getSecondName());
            userCacheRepository.save(userCache);
            log.info("юзер сохранен в post-service");
        }
}
