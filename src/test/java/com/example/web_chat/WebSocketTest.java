package com.example.web_chat;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;

import com.example.web_chat.ChatMessage.ChatMessage;
import com.example.web_chat.ChatTestClient.ChatTestClient;
import com.example.web_chat.ClientMessage.ClientMessage;
import com.example.web_chat.MessageRequest.MessageRequest;
import com.example.web_chat.MessageRequest.MessageRequestType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest
{

    @Value(value = "${local.server.port}")
    private int port;

    @Test
    public void soloConnectAndSubscribeTest() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);
    }

    @Test
    public void soloSendAndRecieveTest() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);
        ClientMessage clientMessage = new ClientMessage("Hello from A");
        clientA.sendMessage(roomName, clientMessage);
        TimeUnit.SECONDS.sleep(3);
        ArrayList<ChatMessage> recievedMessages = clientA.getRecievedMessages();
        // check that client A recieved the message it sent
        assertEquals(recievedMessages.get(0).getText(), clientMessage.getText());
    }

    @Test
    public void duoSendAndRecieveTest() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);
        ChatTestClient clientB = new ChatTestClient(roomName, this.port, websocketURL);
        ClientMessage clientMessageA = new ClientMessage("Hello from A");
        ClientMessage clientMessageB = new ClientMessage("Hello from B");

        clientA.sendMessage(roomName, clientMessageA);
        TimeUnit.SECONDS.sleep(3);

        ArrayList<ChatMessage> recievedMessagesA = clientA.getRecievedMessages();
        ArrayList<ChatMessage> recievedMessagesB = clientB.getRecievedMessages();

        // check that both client A and client B recieved message from client A
        assertEquals(recievedMessagesA.get(0).getText(), clientMessageA.getText());
        assertEquals(recievedMessagesB.get(0).getText(), clientMessageA.getText());

        clientB.sendMessage(roomName, clientMessageB);
        TimeUnit.SECONDS.sleep(3);

        // check that both client A and client B recieved message from client B
        assertEquals(recievedMessagesA.get(1).getText(), clientMessageB.getText());
        assertEquals(recievedMessagesB.get(1).getText(), clientMessageB.getText());
    }

    @Test
    public void soloMessageRequest() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);

        int messageCount = 2;
        ArrayList<ClientMessage> sentClientMessages = new ArrayList<ClientMessage>();
        for (int i = 0; i < messageCount; i++)
        {
            long currentUnixTimestamp = Instant.now().getEpochSecond();
            ClientMessage clientMessage = new ClientMessage("Hello from A from " + currentUnixTimestamp);
            clientA.sendMessage(roomName, clientMessage);
            sentClientMessages.add(clientMessage);
            System.out.println("Sending: " + clientMessage.getText());
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        /*
         * request the 2 sent messages with request type LESS_THAN_TIMESTAMP
         */
        ArrayList<ChatMessage> recievedMessages = clientA.getRecievedMessages();
        long unixTimestamp = recievedMessages.get(recievedMessages.size() - 1).getUnixTimestamp();
        MessageRequest messageRequest = new MessageRequest(unixTimestamp, MessageRequestType.LESS_THAN_TIMESTAMP,
                messageCount);
        clientA.requestMessages(roomName, messageRequest);

        TimeUnit.SECONDS.sleep(3);
        for (int i = 1; i < messageCount + 1; i++)
        {
            System.out.println("Recieved: " + recievedMessages.get(recievedMessages.size() - i).getText());
            assertEquals
            (
                recievedMessages.get(recievedMessages.size() - i).getText(), 
                sentClientMessages.get(sentClientMessages.size() - i).getText()
            );
        }
    }

}
