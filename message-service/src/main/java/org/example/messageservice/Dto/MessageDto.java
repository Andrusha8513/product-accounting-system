package org.example.messageservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.example.messageservice.Status.MessageStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDto {
    private Long id;
    private String chatId;
    private Long senderId;
    private Long recipientId;
    private String text;
    private LocalDateTime date;
    private MessageStatus status;
    private String secondName;
}
