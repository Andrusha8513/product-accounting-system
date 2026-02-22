package com.example.profile_service.kafka;

import com.example.profile_service.ProfileService;
import com.example.profile_service.dto.PostProfileDto;
import com.example.profile_service.entity.Profile;
import com.example.profile_service.dto.PrivetUserProfileDto;
import com.example.profile_service.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileConsumerKafka {
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    @Transactional
    @KafkaListener(topics = "profile",
            groupId = "profile")
    public void consumerProfile(PrivetUserProfileDto profileDto) {
        log.info("Принял профиль из кафки {}", profileDto.getId());

        Profile profile = profileRepository.findById(profileDto.getId())
                .orElse(new Profile());

        profile.setId(profileDto.getId());
        profile.setName(profileDto.getName());
        profile.setSecondName(profileDto.getSecondName());
        profile.setEmail(profileDto.getEmail());
        profile.setPassword(profileDto.getPassword());
        profile.setBirthday(profileDto.getBirthday());
        profileRepository.save(profile);
    }


    @Transactional
    @KafkaListener(topics = "post-to-profile",
            groupId = "profile", containerFactory = "kafkaListenerContainerFactoryPost")
    public void consumerProfilePost(PostProfileDto profileDto) {

        switch (profileDto.getActionType()) {
            case CREATE:
                try {
                    log.info("Принял пост из кафки {}", profileDto.getUserId());
                    profileService.savePost(profileDto);
                    log.info("Пост сохранён");
                } catch (RuntimeException e) {
                    log.info("Критическая ошибка: ", e);
                }
                break;

            case UPDATE:
                try {
                    log.info("Принял пост из кафки {}", profileDto.getUserId());
                    profileService.updatePost(profileDto);
                    log.info("Обновление поста прошло успешно {}", profileDto.getUserId());
                } catch (RuntimeException e) {
                    log.info("Критическая ошибка: ", e);
                }
                break;

            case DELETE:
                try {
                    log.info("Принял пост из кафки {}", profileDto.getUserId());
                    profileService.deletePost(profileDto.getId());
                    log.info("Удаление прошло успешно");
                } catch (RuntimeException e) {
                    log.info("Критическая ошибка: ", e);
                }
        }

    }
}
