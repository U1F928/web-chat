package com.example.web_chat;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;

import com.example.web_chat.ChatMessage.ChatMessage;
import com.example.web_chat.ChatTestClient.ChatTestClient;
import com.example.web_chat.ClientMessage.ClientMessage;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest
{

    @Value(value = "${local.server.port}")
    private int port;

    @Test
    public void soloSubscribeSendRecieveTest() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);
        ClientMessage clientMessage = new ClientMessage("Hello");
        clientA.sendMessage(roomName, clientMessage);
        TimeUnit.SECONDS.sleep(3);
        ArrayList<ChatMessage> recievedMessages = clientA.getRecievedMessages();
        assertEquals(recievedMessages.get(0).getText(), clientMessage.getText());
    }

    
}
