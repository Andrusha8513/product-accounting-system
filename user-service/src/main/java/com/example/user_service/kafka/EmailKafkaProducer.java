package com.example.user_service.kafka;

import com.example.user_service.dto.EmailRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailKafkaProducer {

    private final KafkaTemplate<String , EmailRequestDto> kafkaTemplate;


    public void sendEmailToKafka(EmailRequestDto emailRequestDto){
        kafkaTemplate.send("email" , emailRequestDto);
        log.info("Отправил сообщение в kafka: to={}" , emailRequestDto.getTo());
    }
}
