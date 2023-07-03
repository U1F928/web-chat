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
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByIDDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByIDType;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByTimestampDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByTimestampType;
import com.example.web_chat.PresentationLayer.DTO.Outgoing.ChatMessageDTO;

/*
    On code duplication in tests: 
    https://stackoverflow.com/questions/129693/is-duplicated-code-more-tolerable-in-unit-tests
*/

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
        ClientMessageDTO clientMessageA = new ClientMessageDTO("Hello from A");
        clientA.sendMessage(clientMessageA);
        TimeUnit.SECONDS.sleep(3);
        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedMessages());
    }

    @Test
    public void duoSendAndRecieveTest() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        ChatTestClient clientB = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        ClientMessageDTO clientMessageA = new ClientMessageDTO("Hello from A");
        ClientMessageDTO clientMessageB = new ClientMessageDTO("Hello from B");

        clientA.sendMessage(clientMessageA);
        TimeUnit.SECONDS.sleep(3);

        List<ClientMessageDTO> sentMessages = clientA.getSentMessages();
        // check that both client A and client B recieved message from client A
        assertSentMessagesEqualRequested(sentMessages, clientA.getRecievedMessages());
        assertSentMessagesEqualRequested(sentMessages, clientB.getRecievedMessages());

        // TODO create a client function sendMessage() that takes a String as argument
        clientB.sendMessage(clientMessageB);
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
            ClientMessageDTO clientMessage = new ClientMessageDTO("Hello from A from " + currentUnixTimestamp);
            clientA.sendMessage(clientMessage);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        assertEquals(List.of(), clientA.getRecievedRequestedMessages());

        // TODO put the creation of the MessageRequest instance and sending 
        //      of the request as a client function
        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestByTimestampDTO messageRequest = new MessageRequestByTimestampDTO(currentUnixTimestamp, MessageRequestByTimestampType.LESS_THAN_TIMESTAMP,
                messageCount);
        clientA.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());
    }

    @Test
    public void requestNonExistentMessagesWithLessThanTimestamp() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestByTimestampDTO messageRequest = new MessageRequestByTimestampDTO(currentUnixTimestamp, MessageRequestByTimestampType.LESS_THAN_TIMESTAMP, 10);
        clientA.requestMessages(messageRequest);
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
            ClientMessageDTO clientMessage = new ClientMessageDTO("Hello from A from " + currentUnixTimestamp);
            clientA.sendMessage(clientMessage);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        long unixTimestampOfFirstMessageSent = clientA.getRecievedMessages().get(0).getCreationTimestamp();
        MessageRequestByTimestampDTO messageRequest = new MessageRequestByTimestampDTO(unixTimestampOfFirstMessageSent,
                MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP, messageCount);
        clientA.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());
    }

    @Test
    public void requestNonExistentMessagesWithGreaterThanTimestamp() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        MessageRequestByTimestampDTO messageRequest = new MessageRequestByTimestampDTO(0,
                MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP, 10);
        clientA.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);

        List<ChatMessageDTO> recievedRequestedTexts = clientA.getRecievedRequestedMessages();
        assertEquals(List.of(), recievedRequestedTexts);
    }

    @Test
    public void checkThatOnlyRelevantClientRecievesRequestedMessage() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        ChatTestClient clientB = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        ClientMessageDTO clientMessageA = new ClientMessageDTO("Hello from A");

        clientA.sendMessage(clientMessageA);
        TimeUnit.SECONDS.sleep(3);

        assert (clientA.getRecievedRequestedMessages().isEmpty());
        assert (clientB.getRecievedRequestedMessages().isEmpty());

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestByTimestampDTO messageRequest = new MessageRequestByTimestampDTO(currentUnixTimestamp, MessageRequestByTimestampType.LESS_THAN_TIMESTAMP,
                1);
        clientB.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);

        assert (clientA.getRecievedRequestedMessages().isEmpty());
        assertEquals(1, clientB.getRecievedRequestedMessages().size());
    }

    @Test
    public void checkThatMessageCountLimitIsApplied() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

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
        int messageCountLimit = 2;
        MessageRequestByTimestampDTO messageRequest = new MessageRequestByTimestampDTO(unixTimestampOfFirstMessageSent,
                MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP, messageCountLimit);
        clientA.requestMessages(messageRequest);
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
        ClientMessageDTO clientMessageA = new ClientMessageDTO("Hello from A");

        clientA.sendMessage(clientMessageA);
        TimeUnit.SECONDS.sleep(3);

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestByTimestampDTO messageRequest = new MessageRequestByTimestampDTO(currentUnixTimestamp, MessageRequestByTimestampType.LESS_THAN_TIMESTAMP,
                1);

        clientB.requestMessages(messageRequest);
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
            ClientMessageDTO clientMessage = new ClientMessageDTO("Hello " + i);
            clientA.sendMessage(clientMessage);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        long id = messageCount;
        MessageRequestByIDDTO messageRequest = new MessageRequestByIDDTO(id, MessageRequestByIDType.LESS_THAN_ID,
                messageCount);
        clientA.requestMessages(messageRequest);
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
            ClientMessageDTO clientMessage = new ClientMessageDTO("Hello " + i);
            clientA.sendMessage(clientMessage);
            TimeUnit.SECONDS.sleep(1);
        }
        TimeUnit.SECONDS.sleep(3);

        long id = 0;
        MessageRequestByIDDTO messageRequest = new MessageRequestByIDDTO(id, MessageRequestByIDType.GREATER_THAN_ID,
                messageCount);
        clientA.requestMessages(messageRequest);
        TimeUnit.SECONDS.sleep(3);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());
    }
}