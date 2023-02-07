package com.example.web_chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController
{

    private SimpMessagingTemplate template;

    @Autowired
    public ChatController(SimpMessagingTemplate template)
    {
        this.template = template;
    }

    @MessageMapping("/room/{room}")
    public void publish(@DestinationVariable String room, UserMessage message) throws Exception
    {
        ChatMessage messageOut = new ChatMessage("Your message:" + message.getText());
        System.out.println("Got message\n\n");
        this.template.convertAndSend("/topic/room/" + room, messageOut);
    }

}