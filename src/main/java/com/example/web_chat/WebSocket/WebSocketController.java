package com.example.web_chat.WebSocket;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.example.web_chat.ClientMessage.ClientMessage;
import com.example.web_chat.ClientMessage.ClientMessageService;

import com.example.web_chat.ChatMessage.ChatMessage;
import com.example.web_chat.MessageRequest.MessageRequest;
import com.example.web_chat.MessageRequest.MessageRequestService;

@Controller
public class WebSocketController
{
    @Autowired
    private ClientMessageService clientMessageService;

    @Autowired
    private MessageRequestService messageRequestService;

    @MessageMapping("/room/{roomName}/publish_message") @SendTo("/topic/room/{roomName}")
    public ChatMessage publishMessage(@DestinationVariable String roomName, @Payload ClientMessage clientMessage)
            throws Exception
    {
        System.out.println("Got message\n\n");
        System.out.println("\n\n For room:" + roomName + "\n\n");
        ChatMessage newChatMessage = this.clientMessageService.process(roomName, clientMessage);
        return newChatMessage;
    }

    @MessageMapping("/room/{roomName}/request_messages")
    @SendToUser("/topic/requested_messages")
    public List<ChatMessage> requestMessages(@DestinationVariable String roomName, @Payload MessageRequest messageRequest)
    {
        System.out.println("\n\n Got message request \n\n");
        System.out.println("\n\n For room:" + roomName + "\n\n");
        System.out.println("Request type: " + messageRequest.getRequestType());
        List<ChatMessage> requestedMessages = this.messageRequestService.process(roomName, messageRequest);
        return requestedMessages;
    }
}