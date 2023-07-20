package com.example.web_chat.PresentationLayer.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.example.web_chat.DataLayer.Entity.ChatMessage;
import com.example.web_chat.PresentationLayer.DTO.Incoming.ClientMessageDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByIDDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByTimestampDTO;
import com.example.web_chat.PresentationLayer.DTO.Mapper.ChatMessageMapper;
import com.example.web_chat.PresentationLayer.DTO.Outgoing.ChatMessageDTO;
import com.example.web_chat.BusinessLayer.ClientMessageService;
import com.example.web_chat.BusinessLayer.MessageRequestService;

@Controller
public class WebSocketController
{
    @Autowired
    private ClientMessageService clientMessageService;

    @Autowired
    private MessageRequestService messageRequestService;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @MessageMapping("/room/{roomName}/publish_message") 
    // For topic segment separators 'AMQP 0-9-1' used by RabbitMQ uses dots
    @SendTo("/topic/room.{roomName}")
    public ChatMessageDTO publishMessage(@DestinationVariable String roomName, @Payload ClientMessageDTO clientMessage)
            throws Exception
    {
        ChatMessage newChatMessage = this.clientMessageService.process(roomName, clientMessage);
        ChatMessageDTO chatMessageDTO = this.chatMessageMapper.convertToDTO(newChatMessage);
        return chatMessageDTO;
    }

    @MessageMapping("/room/{roomName}/request_messages_by_timestamp")
    @SendToUser("/topic/requested_messages")
    public List<ChatMessageDTO> requestMessagesByTimestamp(@DestinationVariable String roomName, @Payload MessageRequestByTimestampDTO messageRequest)
    {
        List<ChatMessage> requestedMessages = this.messageRequestService.process(roomName, messageRequest);
        List<ChatMessageDTO> requestedMessageDTOs = this.chatMessageMapper.convertToDTO(requestedMessages);
        return requestedMessageDTOs;
    }

    @MessageMapping("/room/{roomName}/request_messages_by_id")
    @SendToUser("/topic/requested_messages")
    public List<ChatMessageDTO> requestMessagesByID(@DestinationVariable String roomName, @Payload MessageRequestByIDDTO messageRequest)
    {
        List<ChatMessage> requestedMessages = this.messageRequestService.process(roomName, messageRequest);
        List<ChatMessageDTO> requestedMessageDTOs = this.chatMessageMapper.convertToDTO(requestedMessages);
        return requestedMessageDTOs;
    }
}