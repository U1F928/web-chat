package com.example.web_chat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
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
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByIDType;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByTimestampType;
import com.example.web_chat.PresentationLayer.DTO.Outgoing.ChatMessageDTO;

// set active Spring profile to "test", i.e. use application-test.properties
@ActiveProfiles("test")
/* 
    recreate Spring context before each test method, causes warnings 
    https://stackoverflow.com/questions/28105803/tomcat8-memory-leak/
*/
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest
{

    @Value(value = "${local.server.port}")
    private int port;

    private String roomName;

    private String websocketURL;

    public void assertSentMessagesEqualRequested(List<ClientMessageDTO> clientMessages, List<ChatMessageDTO> chatMessages)
    {
        List<String> clientMessagesAsText = clientMessages.stream().map(ClientMessageDTO::getText).toList();
        List<String> chatMessagesAsText = chatMessages.stream().map(ChatMessageDTO::getText).toList();
        assertArrayEquals(clientMessagesAsText.toArray(), chatMessagesAsText.toArray());
    }


    @BeforeEach
    public void init()
    {
        this.roomName = "Cats";
        this.websocketURL = "http://localhost:{port}/websocket";
    }

    @Test
    public void connectAndSubscribeTest() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
    }

    @Test
    public void soloSendAndRecieveTest() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        clientA.sendMessage("Hello from A");
        TimeUnit.SECONDS.sleep(3);
        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedMessages());
    }

    @Test
    public void duoSendAndRecieveTest() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        ChatTestClient clientB = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        clientA.sendMessage("Hello from A");
        TimeUnit.SECONDS.sleep(3);

        List<ClientMessageDTO> sentMessages = clientA.getSentMessages();
        // check that both client A and client B recieved message from client A
        assertSentMessagesEqualRequested(sentMessages, clientA.getRecievedMessages());
        assertSentMessagesEqualRequested(sentMessages, clientB.getRecievedMessages());

        clientB.sendMessage("Hello from B");
        TimeUnit.SECONDS.sleep(3);

        sentMessages = new ArrayList<ClientMessageDTO>();
        sentMessages.addAll(clientA.getSentMessages());
        sentMessages.addAll(clientB.getSentMessages());
        assertSentMessagesEqualRequested(sentMessages, clientA.getRecievedMessages());
        assertSentMessagesEqualRequested(sentMessages, clientB.getRecievedMessages());

    }

    @Test
    public void messageRequestLessThanTimestamp() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            long currentUnixTimestamp = Instant.now().getEpochSecond();
            clientA.sendMessage("Hello from A from " + currentUnixTimestamp);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        assertEquals(List.of(), clientA.getRecievedRequestedMessages());

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.LESS_THAN_TIMESTAMP;
        clientA.requestMessages(currentUnixTimestamp, requestType, messageCount);
        TimeUnit.SECONDS.sleep(3);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());
    }

    @Test
    public void requestNonExistentMessagesWithLessThanTimestamp() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.LESS_THAN_TIMESTAMP;
        clientA.requestMessages(currentUnixTimestamp, requestType, 10);
        TimeUnit.SECONDS.sleep(3);

        List<ChatMessageDTO> recievedRequestedTexts = clientA.getRecievedRequestedMessages();
        assertEquals(List.of(), recievedRequestedTexts);
    }

    @Test
    public void messageRequestGreaterThanTimestamp() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            long currentUnixTimestamp = Instant.now().getEpochSecond();
            clientA.sendMessage("Hello from A from " + currentUnixTimestamp);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        long creationTimestampOfFirstMessageSent = clientA.getRecievedMessages().get(0).getCreationTimestamp();
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP;
        clientA.requestMessages(creationTimestampOfFirstMessageSent, requestType, messageCount);
        TimeUnit.SECONDS.sleep(3);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());
    }

    @Test
    public void requestNonExistentMessagesWithGreaterThanTimestamp() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP;
        clientA.requestMessages(0, requestType, 10);
        TimeUnit.SECONDS.sleep(3);

        List<ChatMessageDTO> recievedRequestedTexts = clientA.getRecievedRequestedMessages();
        assertEquals(List.of(), recievedRequestedTexts);
    }

    @Test
    public void checkThatOnlyRelevantClientRecievesRequestedMessage() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        ChatTestClient clientB = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        clientA.sendMessage("Hello from A");
        TimeUnit.SECONDS.sleep(3);

        assertEquals(List.of(), clientA.getRecievedRequestedMessages());
        assertEquals(List.of(), clientB.getRecievedRequestedMessages());

        int requestedMessageCountLimit = 1;
        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.LESS_THAN_TIMESTAMP;
        clientB.requestMessages(currentUnixTimestamp, requestType, requestedMessageCountLimit);
        TimeUnit.SECONDS.sleep(3);

        assertEquals(List.of(), clientA.getRecievedRequestedMessages());
        assertEquals(requestedMessageCountLimit, clientB.getRecievedRequestedMessages().size());
    }

    @Test
    public void checkThatMessageCountLimitIsApplied() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            long currentUnixTimestamp = Instant.now().getEpochSecond();
            clientA.sendMessage("Hello from A from " + currentUnixTimestamp);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        long creationTimestampOfFirstMessageSent = clientA.getRecievedMessages().get(0).getCreationTimestamp();
        int messageCountLimit = 2;
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP;
        clientA.requestMessages(creationTimestampOfFirstMessageSent, requestType, messageCountLimit);
        TimeUnit.SECONDS.sleep(3);

        List<ChatMessageDTO> recievedRequestedTexts = clientA.getRecievedRequestedMessages(); 
        assertEquals(recievedRequestedTexts.size(), messageCountLimit);
    }

    @Test
    public void requestMessagesFromNonExistentChatRoom() throws Exception
    {
        String nonExistentRoomName = "NON EXISTENT ROOM NAME";
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        ChatTestClient clientB = new ChatTestClient(nonExistentRoomName, this.port, this.websocketURL);

        clientA.sendMessage("Hello from A");
        TimeUnit.SECONDS.sleep(3);

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.LESS_THAN_TIMESTAMP;
        clientB.requestMessages(currentUnixTimestamp, requestType, 10);
        TimeUnit.SECONDS.sleep(3);

        assertEquals(List.of(), clientB.getRecievedRequestedMessages());

    }

    @Test
    public void messageRequestLessThanId() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            clientA.sendMessage("Hello " + i);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        long id = messageCount;
        MessageRequestByIDType requestType = MessageRequestByIDType.LESS_THAN_ID;
        clientA.requestMessages(id, requestType, messageCount);
        TimeUnit.SECONDS.sleep(3);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());
    }

    @Test
    public void messageRequestGreaterThanId() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 0;
        for (int i = 0; i < messageCount; i++)
        {
            clientA.sendMessage("Hello " + i);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        long id = 0;
        MessageRequestByIDType requestType = MessageRequestByIDType.GREATER_THAN_ID;
        clientA.requestMessages(id, requestType, messageCount);
        TimeUnit.SECONDS.sleep(3);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());
    }
}