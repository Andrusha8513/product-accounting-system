package com.example.email_service.kafka;

import com.example.email_service.EmailService;
import com.example.email_service.dto.EmailRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailKafkaConsumer {
    private final EmailService emailService;

    @KafkaListener(topics = "email",
            groupId = "email-service")
    public void consumerEmail(EmailRequestDto emailRequestDto) {
        try {
            log.info("Получено письмо от: email={}", emailRequestDto.getTo());
            emailService.sendConfirmationEmail(emailRequestDto);
            log.info("EmailService вызван успешно");
        } catch (Exception e) {
            log.error("Критическая ошибка при отправке почты: ", e);
        }
    }
}
