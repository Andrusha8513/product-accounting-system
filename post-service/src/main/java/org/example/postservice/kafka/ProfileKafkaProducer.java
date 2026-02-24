package org.example.postservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postservice.dto.PostProfileDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileKafkaProducer {
    private final KafkaTemplate<String , PostProfileDto> kafkaTemplate;

    public void sendPostToKafka(PostProfileDto postDto){
        kafkaTemplate.send("post-to-profile" , postDto);
        log.info("Отправил пост в kafka to={}:" , postDto.getId());
    }
}
