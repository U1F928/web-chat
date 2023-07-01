package com.example.web_chat.PresentationLayer.DTO.Mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.web_chat.DataLayer.Entity.ChatMessage;
import com.example.web_chat.PresentationLayer.DTO.Outgoing.ChatMessageDTO;

@Component
public class ChatMessageMapper 
{
    public ChatMessageDTO convertToDTO(ChatMessage chatMessage)
    {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO
        (
            chatMessage.getID(),
            chatMessage.getRoom().getRoomName(), 
            chatMessage.getCreationTimestamp(), 
            chatMessage.getText()
        );
        return chatMessageDTO;
    }

    public List<ChatMessageDTO> convertToDTO(List<ChatMessage> chatMessages)
    {
        return chatMessages.stream().map(this::convertToDTO).toList();
    }
}
