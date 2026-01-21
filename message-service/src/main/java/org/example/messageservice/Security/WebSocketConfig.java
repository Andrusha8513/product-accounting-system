package org.example.messageservice.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Включаем обработку сообщений
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Сообщения, начинающиеся с /user, будут уходить клиентам (брокер)
        registry.enableSimpleBroker("/user");
        // Сообщения, начинающиеся с /app, Spring направит в наши @MessageMapping методы
        registry.setApplicationDestinationPrefixes("/app");
        // Префикс для персональных очередей
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Точка подключения для фронтенда
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
