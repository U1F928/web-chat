package com.example.web_chat;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.example.web_chat.ChatTestClient.ChatTestClient;
import com.example.web_chat.PresentationLayer.DTO.Incoming.ClientMessageDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestType;
import com.example.web_chat.PresentationLayer.DTO.Outgoing.ChatMessageDTO;

// set active Spring profile to "test", i.e. use application-test.properties
@ActiveProfiles("test")
// recreate Spring context before each test method
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest
{

    @Value(value = "${local.server.port}")
    private int port;

    @Test
    public void connectAndSubscribeTest() throws Exception
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
        ClientMessageDTO clientMessage = new ClientMessageDTO("Hello from A");
        clientA.sendMessage(clientMessage);
        TimeUnit.SECONDS.sleep(3);
        ArrayList<ChatMessageDTO> recievedMessages = clientA.getRecievedMessages();
        // check that client A recieved the message it sent
        assertEquals(clientMessage.getText(), recievedMessages.get(0).getText());
        assertEquals(roomName, recievedMessages.get(0).getRoomName());
    }

    @Test
    public void duoSendAndRecieveTest() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);
        ChatTestClient clientB = new ChatTestClient(roomName, this.port, websocketURL);
        ClientMessageDTO clientMessageA = new ClientMessageDTO("Hello from A");
        ClientMessageDTO clientMessageB = new ClientMessageDTO("Hello from B");

        clientA.sendMessage(clientMessageA);
        TimeUnit.SECONDS.sleep(3);

        ArrayList<ChatMessageDTO> recievedMessagesA = clientA.getRecievedMessages();
        ArrayList<ChatMessageDTO> recievedMessagesB = clientB.getRecievedMessages();

        // check that both client A and client B recieved message from client A
        assertEquals(recievedMessagesA.get(0).getText(), clientMessageA.getText());
        assertEquals(recievedMessagesB.get(0).getText(), clientMessageA.getText());

        clientB.sendMessage(clientMessageB);
        TimeUnit.SECONDS.sleep(3);

        // check that both client A and client B recieved message from client B
        assertEquals(recievedMessagesA.get(1).getText(), clientMessageB.getText());
        assertEquals(recievedMessagesB.get(1).getText(), clientMessageB.getText());
    }

    @Test
    public void messageRequestLessThanTimestamp() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            long currentUnixTimestamp = Instant.now().getEpochSecond();
            ClientMessageDTO clientMessage = new ClientMessageDTO("Hello from A from " + currentUnixTimestamp);
            clientA.sendMessage(clientMessage);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestDTO messageRequest = new MessageRequestDTO(currentUnixTimestamp, MessageRequestType.LESS_THAN_TIMESTAMP,
                messageCount);
        clientA.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);

        List<String> sentTexts = clientA.getSentMessages().stream().map(ClientMessageDTO::getText).toList();
        List<String> recievedRequestedTexts = clientA.getRecievedRequestedMessages().stream().map(ChatMessageDTO::getText)
                .toList();
        assertEquals(sentTexts, recievedRequestedTexts);
    }

    @Test
    public void requestNonExistentMessagesWithLessThanTimestamp() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestDTO messageRequest = new MessageRequestDTO(currentUnixTimestamp, MessageRequestType.LESS_THAN_TIMESTAMP, 10);
        clientA.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);

        List<String> recievedRequestedTexts = clientA.getRecievedRequestedMessages().stream().map(ChatMessageDTO::getText)
                .toList();
        assert (recievedRequestedTexts.isEmpty());
    }

    @Test
    public void messageRequestGreaterThanTimestamp() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            long currentUnixTimestamp = Instant.now().getEpochSecond();
            ClientMessageDTO clientMessage = new ClientMessageDTO("Hello from A from " + currentUnixTimestamp);
            clientA.sendMessage(clientMessage);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        long unixTimestampOfFirstMessageSent = clientA.getRecievedMessages().get(0).getCreationTimestamp();
        MessageRequestDTO messageRequest = new MessageRequestDTO(unixTimestampOfFirstMessageSent,
                MessageRequestType.GREATER_THAN_TIMESTAMP, messageCount);
        clientA.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);

        List<String> sentTexts = clientA.getSentMessages().stream().map(ClientMessageDTO::getText).toList();
        List<String> recievedRequestedTexts = clientA.getRecievedRequestedMessages().stream().map(ChatMessageDTO::getText)
                .toList();
        assertEquals(sentTexts, recievedRequestedTexts);
    }

    @Test
    public void requestNonExistentMessagesWithGreaterThanTimestamp() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);

        MessageRequestDTO messageRequest = new MessageRequestDTO(0,
                MessageRequestType.GREATER_THAN_TIMESTAMP, 10);
        clientA.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);

        List<String> recievedRequestedTexts = clientA.getRecievedRequestedMessages().stream().map(ChatMessageDTO::getText)
                .toList();
        assert (recievedRequestedTexts.isEmpty());
    }

    @Test
    public void assureOnlyRelevantClientRecievesRequestedMessage() throws Exception
    {
        String roomName = "Cats";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);
        ChatTestClient clientB = new ChatTestClient(roomName, this.port, websocketURL);
        ClientMessageDTO clientMessageA = new ClientMessageDTO("Hello from A");

        clientA.sendMessage(clientMessageA);
        TimeUnit.SECONDS.sleep(3);

        assert (clientA.getRecievedRequestedMessages().isEmpty());
        assert (clientB.getRecievedRequestedMessages().isEmpty());

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestDTO messageRequest = new MessageRequestDTO(currentUnixTimestamp, MessageRequestType.LESS_THAN_TIMESTAMP,
                1);
        clientB.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);

        assert (clientA.getRecievedRequestedMessages().isEmpty());
        assertEquals(1, clientB.getRecievedRequestedMessages().size());
    }

    @Test
    public void requestMessagesFromNonExistentChatRoom() throws Exception
    {
        String roomName = "Cats";
        String nonExistentRoomName = "NON EXISTENT ROOM NAME";
        String websocketURL = "http://localhost:{port}/websocket";
        ChatTestClient clientA = new ChatTestClient(roomName, this.port, websocketURL);
        ChatTestClient clientB = new ChatTestClient(nonExistentRoomName, this.port, websocketURL);
        ClientMessageDTO clientMessageA = new ClientMessageDTO("Hello from A");

        clientA.sendMessage(clientMessageA);
        TimeUnit.SECONDS.sleep(3);

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestDTO messageRequest = new MessageRequestDTO(currentUnixTimestamp, MessageRequestType.LESS_THAN_TIMESTAMP,
                1);

        clientB.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);
        assert (clientB.getRecievedRequestedMessages().isEmpty());

    }

}