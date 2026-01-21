package org.example.messageservice.Service;

import org.example.messageservice.Dto.MessageDto;

import java.util.List;

public interface MessageService {
    MessageDto save(MessageDto messageDto);
    MessageDto update(Long id ,String text);
    void deleteById(Long id);
    List<MessageDto> findMessages(Long senderId, Long recipientId);
}
