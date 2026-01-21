package org.example.messageservice.Service.impl;

import org.example.messageservice.Model.ChatRoom;
import org.example.messageservice.Repository.ChatRoomRepository;
import org.example.messageservice.Service.ChatRoomService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    public final ChatRoomRepository chatRoomRepository;
    public ChatRoomServiceImpl(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }


    public Optional<String> getChatId(Long senderId, Long recipientId, boolean createIfNotExist) {
        return chatRoomRepository.findBySenderIdAndRecipientId(senderId , recipientId)
                .map(ChatRoom ::getChatId)
                .or(() -> {
                    if (!createIfNotExist) {
                        return Optional.empty();
                    }
                    String chatId = String.format("%d_%d", senderId, recipientId);

                    ChatRoom senderRecipient = new ChatRoom();
                    senderRecipient.setChatId(chatId);
                    senderRecipient.setSenderId(senderId);
                    senderRecipient.setRecipientId(recipientId);

                    ChatRoom recipientSender = new ChatRoom();
                    recipientSender.setChatId(chatId);
                    recipientSender.setSenderId(recipientId);
                    recipientSender.setRecipientId(senderId);

                    chatRoomRepository.save(senderRecipient);
                    chatRoomRepository.save(recipientSender);

                    return Optional.of(chatId);
                });
    }
}
