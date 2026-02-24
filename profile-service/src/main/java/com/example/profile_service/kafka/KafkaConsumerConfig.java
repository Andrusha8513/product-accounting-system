package com.example.profile_service.kafka;


import com.example.profile_service.dto.PostProfileDto;
import com.example.profile_service.dto.PrivetUserProfileDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, PrivetUserProfileDto> consumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "profile");

        JsonDeserializer<PrivetUserProfileDto> jsonDeserializer = new JsonDeserializer<>(PrivetUserProfileDto.class, objectMapper);
        jsonDeserializer.addTrustedPackages("com.example.profile_service.dto");

        return new DefaultKafkaConsumerFactory<>(
                properties,
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PrivetUserProfileDto> kafkaListenerContainerFactory(
            ConsumerFactory<String, PrivetUserProfileDto> consumerFactory
    ) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, PrivetUserProfileDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }


    @Bean
    public ConsumerFactory<String, PostProfileDto> consumerFactoryPost(ObjectMapper objectMapper) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "profile");

        JsonDeserializer<PostProfileDto> jsonDeserializer = new JsonDeserializer<>(PostProfileDto.class, objectMapper);
        jsonDeserializer.addTrustedPackages("com.example.profile_service.kafka");

        return new DefaultKafkaConsumerFactory<>(
                properties,
                new JsonDeserializer<>(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostProfileDto> kafkaListenerContainerFactoryPost(
            ConsumerFactory<String, PostProfileDto> consumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, PostProfileDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }


}
