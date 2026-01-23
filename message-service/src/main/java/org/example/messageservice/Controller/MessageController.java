package org.example.messageservice.Controller;

import org.example.messageservice.Dto.MessageDto;
import org.example.messageservice.Service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    public MessageController(MessageService messageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    /// Отправка сообщений
    @MessageMapping("/chat")
    public void processMessage(@Payload MessageDto messageDto , Principal principal) {
        principal.getName();
        MessageDto savedMsg = messageService.save(messageDto);
        /// Отправляем получателю в реальном времени
        simpMessagingTemplate.convertAndSendToUser(
                savedMsg.getRecipientId().toString(),
                "/queue/messages",
                savedMsg
        );
    }
    /// Для прогрузки сообщений в чате
    @GetMapping("/history/{senderId}/{recipientId}")
    public ResponseEntity<List<MessageDto>> getHistory(@PathVariable Long senderId , @PathVariable Long recipientId) {
        return ResponseEntity.ok(messageService.findMessages(senderId, recipientId));
    }
    /// удаление
    @DeleteMapping("/deleted/{id}")
    public void deleteMessage(@PathVariable Long id ) {
        messageService.deleteById(id);
    }
    /// Обновление
    @PostMapping("/updated/{id}")
    public ResponseEntity<MessageDto> updateMessage(@PathVariable Long id, @RequestBody String text) {
        MessageDto updatedMsg = messageService.update(id, text);
        return ResponseEntity.ok(updatedMsg);
    }
}
