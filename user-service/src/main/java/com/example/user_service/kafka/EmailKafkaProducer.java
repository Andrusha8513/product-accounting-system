package com.example.user_service.kafka;

import com.example.user_service.dto.EmailRequestDto;
import com.example.user_service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailKafkaProducer {

    private final KafkaTemplate<String , EmailRequestDto> kafkaTemplate;
    private final KafkaTemplate<String, UserDto> kafkaTemplateUser;


    public void sendEmailToKafka(EmailRequestDto emailRequestDto){
        kafkaTemplate.send("email" , emailRequestDto);
        log.info("Отправил сообщение в kafka: to={}" , emailRequestDto.getTo());
    }

    public void sendEmailToUser(UserDto userDto){
        kafkaTemplateUser.send("user" , userDto);
        log.info("Отправил сообщение в kafka: to={}" , userDto.getId());
    }
}
