package com.example.web_chat;

import java.time.Instant;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController
{
    @MessageMapping("/room/{roomName}/publish") @SendTo("/topic/room/{roomName}")
    public ChatMessage publish(@DestinationVariable String roomName, ClientMessage clientMessage) throws Exception
    {
        ChatRoom chatRoom = ChatController.getChatRoom(roomName);
        if (chatRoom == null)
        {
            chatRoom = ChatController.createChatRoom(roomName);
        }

        ChatMessage newChatMessage = ChatController.createChatMessage(chatRoom, clientMessage);

        System.out.println("Got message\n\n");

        return newChatMessage;
    }

    private static ChatRoom getChatRoom(String roomName)
    {
        Session session = HibernateUtil.getSession();
        ChatRoom chatRoom = session.get(ChatRoom.class, roomName);
        return chatRoom;
    }

    private static ChatRoom createChatRoom(String roomName)
    {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();

        ChatRoom newChatRoom = new ChatRoom(roomName);

        session.persist(newChatRoom);
        transaction.commit();

        return newChatRoom;
    }

    private static ChatMessage createChatMessage(ChatRoom chatRoom, ClientMessage clientMessage)
    {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        ChatMessage newChatMessage = new ChatMessage(chatRoom, currentUnixTimestamp, clientMessage.getText());

        session.persist(newChatMessage);
        transaction.commit();

        return newChatMessage;
    }

}