package com.example.web_chat.MessageRequest;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.web_chat.ChatMessage.ChatMessage;
import com.example.web_chat.ChatMessage.ChatMessageRepository;
import com.example.web_chat.ChatRoom.ChatRoom;
import com.example.web_chat.ChatRoom.ChatRoomRepository;

@Service
public class MessageRequestService
{
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public List<ChatMessage> process(String roomName, MessageRequest messageRequest)
    {
        ChatRoom chatRoom = this.chatRoomRepository.findByRoomName(roomName);
        if(messageRequest.getRequestType() == MessageRequestType.LESS_THAN_TIMESTAMP)
        {
            Pageable firstN = PageRequest.of(0, messageRequest.getMessageCount());
            List<ChatMessage> requestedMessages = this.chatMessageRepository.findByRoomAndUnixTimestampLessThan(chatRoom, messageRequest.getUnixTimestamp(), firstN);
            Collections.reverse(requestedMessages);
            return requestedMessages;
        }
        else if(messageRequest.getRequestType() == MessageRequestType.GREATER_THAN_TIMESTAMP)
        {
            Pageable firstN = PageRequest.of(0, messageRequest.getMessageCount());
            return this.chatMessageRepository.findByRoomAndUnixTimestampGreaterThan(chatRoom, messageRequest.getUnixTimestamp(), firstN);
        }

        // TODO don't just return null
        return null;
    }
}