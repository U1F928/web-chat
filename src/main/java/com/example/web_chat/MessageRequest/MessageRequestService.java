package com.example.web_chat.MessageRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.web_chat.ChatMessage.ChatMessage;
import com.example.web_chat.ChatMessage.ChatMessageRepository;

@Service
public class MessageRequestService
{
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> process(String roomName, MessageRequest messageRequest)
    {
        if(messageRequest.getRequestType() == MessageRequestType.GREATER_THAN_TIMESTAMP)
        {
            Pageable firstN = PageRequest.of(0, messageRequest.getMessageCount());
            return this.chatMessageRepository.findByUnixTimestampGreaterThan(messageRequest.getUnixTimestamp(), firstN);
        }
        else if(messageRequest.getRequestType() == MessageRequestType.LESS_THAN_TIMESTAMP)
        {
            Pageable firstN = PageRequest.of(0, messageRequest.getMessageCount());
            return this.chatMessageRepository.findByUnixTimestampLessThan(messageRequest.getUnixTimestamp(), firstN);
        }
        return null;
    }
}