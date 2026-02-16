package com.example.profile_service.kafka;

import com.example.profile_service.Profile;
import com.example.profile_service.dto.PrivetUserProfileDto;
import com.example.profile_service.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileConsumerKafka {
    private final ProfileRepository profileRepository;

    @KafkaListener(topics = "profile",
    groupId = "profile")
    public void consumerProfile(PrivetUserProfileDto profileDto){
        log.info("Принял профиль из кафки {}" , profileDto.getId());

        Profile profile = profileRepository.findByUserId(profileDto.getId())
                        .orElse(new Profile());

        profile.setUserId(profileDto.getId());
        profile.setName(profileDto.getName());
        profile.setSecondName(profileDto.getSecondName());
        profile.setEmail(profileDto.getEmail());
        profile.setPassword(profileDto.getPassword());
        profile.setBirthday(profileDto.getBirthday());
        profileRepository.save(profile);
    }
}
