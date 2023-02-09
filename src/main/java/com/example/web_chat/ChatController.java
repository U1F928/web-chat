package com.example.web_chat;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Controller
public class ChatController
{
    @MessageMapping("/room/{roomName}/publish_message") @SendTo("/topic/room/{roomName}")
    public ChatMessage publishMessage(@DestinationVariable String roomName, @Payload ClientMessage clientMessage)
            throws Exception
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

    @MessageMapping("/room/{roomName}/request_messages")
    @SendToUser("/topic/requested_messages")
    public ArrayList<ChatMessage> requestMessages(@DestinationVariable String roomName, @Payload MessageRequest messageRequest)
    {
        ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
        chatMessages.add(new ChatMessage(null, 0, "sadad"));
        System.out.println("\n\n Got message request \n\n");
        return chatMessages;
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