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
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestDTO;
import com.example.web_chat.BusinessLayer.ClientMessageService;
import com.example.web_chat.BusinessLayer.MessageRequestService;

@Controller
public class WebSocketController
{
    @Autowired
    private ClientMessageService clientMessageService;

    @Autowired
    private MessageRequestService messageRequestService;

    @MessageMapping("/room/{roomName}/publish_message") @SendTo("/topic/room/{roomName}")
    public ChatMessage publishMessage(@DestinationVariable String roomName, @Payload ClientMessageDTO clientMessage)
            throws Exception
    {
        System.out.println("Got message\n\n");
        System.out.println("\n\n For room:" + roomName + "\n\n");
        ChatMessage newChatMessage = this.clientMessageService.process(roomName, clientMessage);
        return newChatMessage;
    }

    @MessageMapping("/room/{roomName}/request_messages")
    @SendToUser("/topic/requested_messages")
    public List<ChatMessage> requestMessages(@DestinationVariable String roomName, @Payload MessageRequestDTO messageRequest)
    {
        System.out.println("\n\n Got message request \n\n");
        System.out.println("\n\n For room:" + roomName + "\n\n");
        System.out.println("Request type: " + messageRequest.getRequestType());
        List<ChatMessage> requestedMessages = this.messageRequestService.process(roomName, messageRequest);
        return requestedMessages;
    }
}