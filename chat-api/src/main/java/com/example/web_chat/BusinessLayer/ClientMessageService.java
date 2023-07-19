package com.example.web_chat.BusinessLayer;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.web_chat.DataLayer.Entity.ChatMessage;
import com.example.web_chat.DataLayer.Entity.ChatRoom;
import com.example.web_chat.DataLayer.Repository.ChatMessageRepository;
import com.example.web_chat.DataLayer.Repository.ChatRoomRepository;
import com.example.web_chat.PresentationLayer.DTO.Incoming.ClientMessageDTO;

@Service
public class ClientMessageService
{
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatMessage process(String roomName, ClientMessageDTO clientMessage)
    {
        ChatRoom chatRoom = this.getChatRoom(roomName);
        if (chatRoom == null)
        {
            chatRoom = this.createChatRoom(roomName);
        }
        ChatMessage newChatMessage = this.createChatMessage(chatRoom, clientMessage);
        return newChatMessage;
    }

    private ChatRoom getChatRoom(String roomName)
    {
        ChatRoom chatRoom = this.chatRoomRepository.findByRoomName(roomName);
        return chatRoom;
    }

    private ChatRoom createChatRoom(String roomName)
    {
        ChatRoom newChatRoom = new ChatRoom(roomName);
        this.chatRoomRepository.save(newChatRoom);
        return newChatRoom;
    }

    private ChatMessage createChatMessage(ChatRoom chatRoom, ClientMessageDTO clientMessage)
    {
        long currentUnixTimestamp = Instant.now().getEpochSecond();
        ChatMessage newChatMessage = new ChatMessage(chatRoom, currentUnixTimestamp, clientMessage.getText());
        this.chatMessageRepository.save(newChatMessage);
        return newChatMessage;
    }
}