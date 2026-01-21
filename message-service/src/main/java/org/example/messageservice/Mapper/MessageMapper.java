package org.example.messageservice.Mapper;

import org.example.messageservice.Dto.MessageDto;
import org.example.messageservice.Model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageDto toDto(Message message){
        MessageDto messageDto = new MessageDto();
        messageDto.setId(message.getId());
        messageDto.setChatId(message.getChatId());
        messageDto.setSenderId(message.getSenderId());
        messageDto.setRecipientId(message.getRecipientId());
        messageDto.setText(message.getText());
        messageDto.setDate(message.getDate());
        messageDto.setStatus(message.getStatus());
        messageDto.setSecondName(message.getSecondName());
        return messageDto;
    }
    public Message toEntity(MessageDto messageDto){
        Message message = new Message();
        message.setId(messageDto.getId());
        message.setChatId(messageDto.getChatId());
        message.setSenderId(messageDto.getSenderId());
        message.setRecipientId(messageDto.getRecipientId());
        message.setText(messageDto.getText());
        message.setDate(messageDto.getDate());
        message.setStatus(messageDto.getStatus());
        message.setSecondName(messageDto.getSecondName());
        return message;
    }

}
