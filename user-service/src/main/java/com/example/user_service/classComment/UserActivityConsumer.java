//package com.example.user_service.kafka;
//
//import com.example.user_service.dto.UserActivityEventDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class UserActivityConsumer {
//    @KafkaListener(
//            topics = "post-events",
//            groupId = "user-service-group",
//            containerFactory = "activityKafkaListenerContainerFactory"
//    )
//    public void handleActivity(UserActivityEventDto event) {
//        log.info("Получено событие: Тип={}, Действие={}, UserID={}",
//                event.getEntityType(), event.getAction(), event.getUserId());
//
//        try {
//            switch (event.getEntityType()) {
//                case "POST" -> handlePost(event);
//                case "COMMENT" -> handleComment(event);
//                case "SUBCOMMENT" -> handleSubComment(event);
//                default -> log.warn("Неизвестный тип сущности: {}", event.getEntityType());
//            }
//        } catch (Exception e) {
//            log.error("Ошибка при обработке события: ", e);
//        }
//    }
//    private void handlePost(UserActivityEventDto event) {
//        switch (event.getAction()) {
//            case CREATE -> log.info("Юзер {} создал пост {}" , event.getUserId(), event.getId());
//            case DELETE -> log.info("Юзер {} удалил пост {}" , event.getUserId(), event.getId());
//            case UPDATE -> log.info("Юзер {} обновил пост {}" , event.getUserId(), event.getId());
//        }
//    }
//    private void handleComment(UserActivityEventDto event) {
//        switch (event.getAction()) {
//            case CREATE -> log.info("Юзер {} добавил комментарий {}" , event.getUserId(), event.getId());
//            case DELETE -> log.info("Юзер {} удалил комментарий {}" , event.getUserId(), event.getId());
//            case UPDATE -> log.info("Юзер {} обновил комментарий {}" , event.getUserId(), event.getId());
//        }
//    }
//    private void handleSubComment(UserActivityEventDto event) {
//        switch (event.getAction()) {
//            case CREATE -> log.info("Юзер {} добавил ответ на комментарий {}" , event.getUserId(), event.getId());
//            case DELETE -> log.info("Юзер {} удалил ответ на комментарий {}" , event.getUserId(), event.getId());
//            case UPDATE -> log.info("Юзер {} обновил ответ на комментарий {}" , event.getUserId(), event.getId());
//        }
//    }
//}

