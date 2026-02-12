package org.example.postservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.postservice.dto.UserDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


@EnableKafka
@Configuration
public class KafkaConsumerConfig {


    @Bean
    public ConsumerFactory<String , UserDto> consumerFactory(ObjectMapper objectMapper){
       Map<String , Object> properties = new HashMap<>();
       properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG , "localhost:9092");
       properties.put(ConsumerConfig.GROUP_ID_CONFIG , "email-service");

        JsonDeserializer<UserDto> jsonDeserializer = new JsonDeserializer<>(UserDto.class ,objectMapper);
        jsonDeserializer.addTrustedPackages("*");


        return  new DefaultKafkaConsumerFactory<>(
                properties ,
                new StringDeserializer(),
                jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , UserDto> kafkaListenerContainerFactory(
            ConsumerFactory<String , UserDto> consumerFactory){
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String , UserDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }
}
