package com.example.web_chat;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @MessageMapping("/room/{roomName}/publish_message") @SendTo("/topic/room/{roomName}")
    public ChatMessage publishMessage(@DestinationVariable String roomName, @Payload ClientMessage clientMessage)
            throws Exception
    {
        ChatRoom chatRoom = this.getChatRoom(roomName);
        if (chatRoom == null)
        {
            chatRoom = this.createChatRoom(roomName);
        }

        ChatMessage newChatMessage = this.createChatMessage(chatRoom, clientMessage);

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

    private ChatRoom getChatRoom(String roomName)
    {
        ChatRoom chatRoom = this.chatRoomRepository.findByRoomName(roomName);
        return chatRoom;
    }

    private ChatRoom createChatRoom(String roomName)
    {
        ChatRoom newChatRoom = new ChatRoom(roomName);
        this.chatRoomRepository.save(newChatRoom);
        return newChatRoom;
    }

    private ChatMessage createChatMessage(ChatRoom chatRoom, ClientMessage clientMessage)
    {
        long currentUnixTimestamp = Instant.now().getEpochSecond();
        ChatMessage newChatMessage = new ChatMessage(chatRoom, currentUnixTimestamp, clientMessage.getText());
        this.chatMessageRepository.save(newChatMessage);
        return newChatMessage;
    }

}