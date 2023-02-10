package com.example.web_chat.WebSocket;

import java.util.ArrayList;

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

@Controller
public class WebSocketController
{
    @Autowired
    private ClientMessageService clientMessageService;

    @MessageMapping("/room/{roomName}/publish_message") @SendTo("/topic/room/{roomName}")
    public ChatMessage publishMessage(@DestinationVariable String roomName, @Payload ClientMessage clientMessage)
            throws Exception
    {
        System.out.println("Got message\n\n");
        ChatMessage newChatMessage = this.clientMessageService.process(roomName, clientMessage);
        return newChatMessage;
    }

    @MessageMapping("/room/{roomName}/request_messages")
    @SendToUser("/topic/requested_messages")
    public ArrayList<ChatMessage> requestMessages(@DestinationVariable String roomName, @Payload MessageRequest messageRequest)
    {
        ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
        chatMessages.add(new ChatMessage(null, 0, "sadad"));
        System.out.println("\n\n Got message request \n\n");
        return chatMessages;
    }
}