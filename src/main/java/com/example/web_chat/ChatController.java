package com.example.web_chat;

import java.time.Instant;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

    @MessageMapping("/room/{roomName}")
    public void publish(@DestinationVariable String roomName, ClientMessage message) throws Exception
    {

        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();

        ChatRoom chatRoom = session.get(ChatRoom.class, roomName);
        long currentUnixTimestamp = Instant.now().getEpochSecond();
        ChatMessage newChatMessage = new ChatMessage(chatRoom, currentUnixTimestamp, message.getText());

        System.out.println("Got message\n\n");

        session.persist(newChatMessage);
        transaction.commit();

        this.template.convertAndSend("/topic/room/" + roomName, newChatMessage);
    }

}