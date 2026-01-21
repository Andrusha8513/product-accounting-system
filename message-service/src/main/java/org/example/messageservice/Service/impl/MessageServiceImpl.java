package org.example.messageservice.Service.impl;

import org.example.messageservice.Dto.MessageDto;
import org.example.messageservice.Dto.UserDto;
import org.example.messageservice.Mapper.MessageMapper;
import org.example.messageservice.Model.Message;
import org.example.messageservice.Repository.ChatRoomRepository;
import org.example.messageservice.Repository.MessageRepository;
import org.example.messageservice.Service.ChatRoomService;
import org.example.messageservice.Service.MessageService;
import org.example.messageservice.Status.MessageStatus;
import org.example.messageservice.UserClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    public final MessageRepository messageRepository;
    public final MessageMapper messageMapper;
    public final ChatRoomService chatRoomService;
    private final UserClient userClient;

    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper,
                              ChatRoomService chatRoomService ,  UserClient userClient) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.chatRoomService = chatRoomService;
        this.userClient = userClient;
    }



    public MessageDto save(MessageDto messageDto) {
        String chatId  = chatRoomService.getChatId(messageDto.getSenderId() ,
                messageDto.getRecipientId() , true).orElseThrow();

        Message message = messageMapper.toEntity(messageDto);
        try{
            UserDto userDto = userClient.getUserBySecondName(messageDto.getSecondName());
            message.setSecondName(userDto.getSecondName());
        }catch (Exception e){

        }
        message.setChatId(chatId);
        message.setDate(LocalDateTime.now());
        message.setStatus(MessageStatus.SENT);

        return messageMapper.toDto(messageRepository.save(message));
    }


    public MessageDto update(Long id, String text) {
        Message message = messageRepository.findById(id).orElseThrow();
        message.setText(text);
        message.setStatus(MessageStatus.EDITED);
        Message updatedMessage = messageRepository.save(message);
        return messageMapper.toDto(updatedMessage);
    }


    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }


    public List<MessageDto> findMessages(Long senderId, Long recipientId) {
        return chatRoomService.getChatId(senderId, recipientId, false)
                .map(id -> messageRepository.findByChatIdOrderByDateAsc(id).stream().map(messageMapper::toDto).toList())
                .orElse(new ArrayList<>());
    }
}
