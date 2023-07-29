package com.example.web_chat.BusinessLayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.web_chat.DataLayer.Entity.ChatMessage;
import com.example.web_chat.DataLayer.Entity.ChatRoom;
import com.example.web_chat.DataLayer.Repository.ChatMessageRepository;
import com.example.web_chat.DataLayer.Repository.ChatRoomRepository;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByIDDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByIDType;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByTimestampDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByTimestampType;

@Service
public class MessageRequestService
{
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public List<ChatMessage> process(String roomName, MessageRequestByTimestampDTO messageRequest)
    {
        ChatRoom chatRoom = this.chatRoomRepository.findByRoomName(roomName);
        if(chatRoom == null)
        {
            return new ArrayList<ChatMessage>();
        }

        int pageSize = messageRequest.getPageSize();
        int pageNumber = messageRequest.getPageNumber();
        if(messageRequest.getRequestType() == MessageRequestByTimestampType.LESS_THAN_TIMESTAMP)
        {
            Pageable firstN = PageRequest.of(pageNumber, pageSize);
            List<ChatMessage> requestedMessages = this.chatMessageRepository.findByRoomAndCreationTimestampLessThan(chatRoom, messageRequest.getCreationTimestamp(), firstN);
            Collections.reverse(requestedMessages);
            return requestedMessages;
        }
        else if(messageRequest.getRequestType() == MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP)
        {
            System.out.println("Got page number: " + pageNumber + " and page size: " + pageSize);
            Pageable firstN = PageRequest.of(pageNumber, pageSize);
            return this.chatMessageRepository.findByRoomAndCreationTimestampGreaterThan(chatRoom, messageRequest.getCreationTimestamp(), firstN);
        }

        return new ArrayList<ChatMessage>();
    }


    public List<ChatMessage> process(String roomName, MessageRequestByIDDTO messageRequest)
    {
        ChatRoom chatRoom = this.chatRoomRepository.findByRoomName(roomName);
        if(chatRoom == null)
        {
            return new ArrayList<ChatMessage>();
        }

        if(messageRequest.getRequestType() == MessageRequestByIDType.LESS_THAN_ID)
        {
            Pageable firstN = PageRequest.of(0, messageRequest.getMessageCountLimit());
            List<ChatMessage> requestedMessages = this.chatMessageRepository.findByRoomAndIDLessThan(chatRoom, messageRequest.getID(), firstN);
            Collections.reverse(requestedMessages);
            return requestedMessages;
        }
        else if(messageRequest.getRequestType() == MessageRequestByIDType.GREATER_THAN_ID)
        {
            Pageable firstN = PageRequest.of(0, messageRequest.getMessageCountLimit());
            return this.chatMessageRepository.findByRoomAndIDGreaterThan(chatRoom, messageRequest.getID(), firstN);
        }

        return new ArrayList<ChatMessage>();
    }
}