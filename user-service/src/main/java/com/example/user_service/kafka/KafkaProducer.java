package com.example.user_service.kafka;

import com.example.user_service.dto.EmailRequestDto;
import com.example.user_service.dto.TestProfileDto;
import com.example.user_service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, EmailRequestDto> kafkaTemplate;

    private final KafkaTemplate<String , UserDto> kafkaTemplateUser;

    private final KafkaTemplate<String , TestProfileDto> kafkaTemplateProfile;

    public void sendEmailToKafka(EmailRequestDto emailRequestDto) {
        kafkaTemplate.send("email", emailRequestDto);
        log.info("Отправил сообщение в kafka: to={}", emailRequestDto.getTo());
    }

    public void sendUserToKafka(UserDto userDto) {
        kafkaTemplateUser.send("user", userDto);
        log.info("Отправил сообщение в kafka: to={}", userDto.getId());
    }

    public void sendPrivetProfileToKafka(TestProfileDto profileDto){
        kafkaTemplateProfile.send("profile" , profileDto);
        log.info("Отправил профиль в кафка: to={}" , profileDto.getEmail());
    }
}
