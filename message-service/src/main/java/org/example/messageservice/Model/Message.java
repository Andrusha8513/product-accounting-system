package org.example.messageservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.messageservice.Status.MessageStatus;

import java.awt.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String chatId;
    private Long senderId;
    private Long recipientId;
    private String text;
    private LocalDateTime date;
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    private String secondName;
}
