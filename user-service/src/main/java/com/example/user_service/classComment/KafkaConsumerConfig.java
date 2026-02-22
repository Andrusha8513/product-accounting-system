//package com.example.user_service.kafka;
//
//import com.example.user_service.dto.UserActivityEventDto;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@EnableKafka
//@Configuration
//public class KafkaConsumerConfig {
//
//    @Bean
//    public ConsumerFactory<String, UserActivityEventDto> activityConsumerFactory(ObjectMapper objectMapper) {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-service-group");
//
//        JsonDeserializer<UserActivityEventDto> jsonDeserializer = new JsonDeserializer<>(UserActivityEventDto.class, objectMapper);
//        jsonDeserializer.addTrustedPackages("*");
//
//        return new DefaultKafkaConsumerFactory<>(
//                props,
//                new StringDeserializer(),
//                jsonDeserializer);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, UserActivityEventDto> activityKafkaListenerContainerFactory(
//            ConsumerFactory<String, UserActivityEventDto> activityConsumerFactory) {
//        var factory = new ConcurrentKafkaListenerContainerFactory<String, UserActivityEventDto>();
//        factory.setConsumerFactory(activityConsumerFactory);
//        return factory;
//    }
//}
