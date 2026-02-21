package com.example.user_service.kafka;



import com.example.user_service.dto.EmailRequestDto;
import com.example.user_service.dto.TestProfileDto;
import com.example.user_service.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, EmailRequestDto> producerFactory(ObjectMapper objectMapper) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        JsonSerializer<EmailRequestDto> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                config,
                new StringSerializer(),
                serializer);
    }
    @Bean
    public KafkaTemplate<String , EmailRequestDto> kafkaTemplate(
            ProducerFactory<String , EmailRequestDto> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }
    @Bean
    public ProducerFactory<String , UserDto> userProducerFactory(ObjectMapper objectMapper){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        JsonSerializer<UserDto> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);
        return new DefaultKafkaProducerFactory<>(
                config,
                new StringSerializer(),
                serializer
        );
    }

    @Bean
    public KafkaTemplate<String , UserDto> kafkaTemplateUser(
            ProducerFactory<String , UserDto> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }




    @Bean
    public ProducerFactory<String , TestProfileDto> profileDtoProducerFactory(ObjectMapper objectMapper){
        Map<String , Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG , "localhost:9092");
        JsonSerializer<TestProfileDto> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                config ,
                new StringSerializer(),
                serializer
        );
    }


    @Bean
    public KafkaTemplate<String , TestProfileDto> kafkaTemplateProfile(
            ProducerFactory<String , TestProfileDto> producerFactory){
                return new KafkaTemplate<>(producerFactory);
    }

}
