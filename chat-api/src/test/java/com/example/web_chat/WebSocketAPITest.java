package com.example.web_chat;

import static org.junit.jupiter.api.Assertions.*;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.example.web_chat.ChatTestClient.ChatTestClient;
import com.example.web_chat.PresentationLayer.DTO.Incoming.ClientMessageDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByIDType;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByTimestampType;
import com.example.web_chat.PresentationLayer.DTO.Outgoing.ChatMessageDTO;

/* 
    recreate Spring context before each test method, causes warnings 
    https://stackoverflow.com/questions/28105803/tomcat8-memory-leak/
*/
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketAPITest
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
        this.websocketURL = "ws://localhost:{port}/websocket";
    }

    @Test
    public void connectAndSubscribeTest() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        clientA.disconnect();
    }


    @Test
    public void soloSendAndRecieveTest() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        clientA.sendMessage("Hello from A");
        Awaitility.await().until(() -> clientA.getRecievedMessages().size() == 1);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedMessages());
        clientA.disconnect();
    }

    @Test
    public void soloSendAndRecieveLongTextTest() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
;
        String messageText = "";
        for(Integer i = 0; i < 2999; i++) messageText += "a";

        clientA.sendMessage(messageText);
        Awaitility.await().until(() -> clientA.getRecievedMessages().size() == 1);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedMessages());
        clientA.disconnect();
    }

    @Test
    public void duoSendAndRecieveTest() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        ChatTestClient clientB = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        clientA.sendMessage("Hello from A");
        Awaitility.await().until(() -> clientA.getRecievedMessages().size() == 1);
        Awaitility.await().until(() -> clientB.getRecievedMessages().size() == 1);

        List<ClientMessageDTO> sentMessages = clientA.getSentMessages();
        // check that both client A and client B recieved message from client A
        assertSentMessagesEqualRequested(sentMessages, clientA.getRecievedMessages());
        assertSentMessagesEqualRequested(sentMessages, clientB.getRecievedMessages());

        clientB.sendMessage("Hello from B");
        Awaitility.await().until(() -> clientA.getRecievedMessages().size() == 2);
        Awaitility.await().until(() -> clientB.getRecievedMessages().size() == 2);

        sentMessages = new ArrayList<ClientMessageDTO>();
        sentMessages.addAll(clientA.getSentMessages());
        sentMessages.addAll(clientB.getSentMessages());
        assertSentMessagesEqualRequested(sentMessages, clientA.getRecievedMessages());
        assertSentMessagesEqualRequested(sentMessages, clientB.getRecievedMessages());

        clientA.disconnect();
        clientB.disconnect();

    }

    @Test
    public void messageRequestLessThanTimestamp() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            clientA.sendMessage("Hello from A " + i);
            Integer objectI = i;
            Awaitility.await().until(() -> clientA.getRecievedMessages().size() == objectI + 1);

        }
        Awaitility.await().until(() -> clientA.getRecievedMessages().size() == messageCount);

        assertEquals(List.of(), clientA.getRecievedRequestedMessages());

        long oneHourInSeconds = 60 * 60;
        long currentUnixTimestamp = Instant.now().getEpochSecond() + oneHourInSeconds;
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.LESS_THAN_TIMESTAMP;
        clientA.requestMessages(currentUnixTimestamp, requestType, messageCount, 0);
        Awaitility.await().until(() -> clientA.getRecievedRequestedMessages().size() == messageCount);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());

        clientA.disconnect();
    }

    @Test
    public void messageRequestLessThanTimestampWithPaging() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            clientA.sendMessage("Hello from A " + i);
            Integer objectI = i;
            Awaitility.await().until(() -> clientA.getRecievedMessages().size() == objectI + 1);
        }

        long oneHourInSeconds = 60 * 60;
        long currentUnixTimestamp = Instant.now().getEpochSecond() + oneHourInSeconds;
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.LESS_THAN_TIMESTAMP;
        for (int i = 0; i < messageCount; i++)
        {
            Integer objectI = i;
            clientA.requestMessages(currentUnixTimestamp, requestType, 1, i);
            Awaitility.await().until(() -> clientA.getRecievedRequestedMessages().size() == objectI + 1);
            List<ChatMessageDTO> requestedReceivedMessages = clientA.getRecievedRequestedMessages();
            ChatMessageDTO lastRecievedRequestedMessage = requestedReceivedMessages.get(requestedReceivedMessages.size() - 1);
            assertEquals(clientA.getSentMessages().get(messageCount - i - 1).getText(), lastRecievedRequestedMessage.getText());
        }

        clientA.disconnect();
    }

    @Test
    public void requestNonExistentMessagesWithLessThanTimestamp() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.LESS_THAN_TIMESTAMP;
        clientA.requestMessages(currentUnixTimestamp, requestType, 10, 0);

        assertThrows
        (
            ConditionTimeoutException.class, 
            () -> 
            {
                Awaitility.await().until(() -> clientA.getRecievedRequestedMessages().size() > 0);
            }
        );

        clientA.disconnect();
    }

    @Test
    public void messageRequestGreaterThanTimestamp() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            clientA.sendMessage("Hello from A " + i);
            Integer objectI = i;
            Awaitility.await().until(() -> clientA.getRecievedMessages().size() == objectI + 1);
        }

        long creationTimestampOfFirstMessageSent = clientA.getRecievedMessages().get(0).getCreationTimestamp();
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP;
        clientA.requestMessages(creationTimestampOfFirstMessageSent - 1, requestType, messageCount, 0);
        Awaitility.await().until(() -> clientA.getRecievedRequestedMessages().size() == messageCount);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());

        clientA.disconnect();
    }

    @Test
    public void messageRequestGreaterThanTimestampWithPaging() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            clientA.sendMessage("Hello from A " + i);
            Integer objectI = i;
            Awaitility.await().until(() -> clientA.getRecievedMessages().size() == objectI + 1);
        }

        long creationTimestampOfFirstMessageSent = clientA.getRecievedMessages().get(0).getCreationTimestamp();
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP;
        for (int i = 0; i < messageCount; i++)
        {
            Integer objectI = i;
            clientA.requestMessages(creationTimestampOfFirstMessageSent - 1, requestType, 1, i);
            Awaitility.await().until(() -> clientA.getRecievedRequestedMessages().size() == objectI + 1);
            List<ChatMessageDTO> requestedReceivedMessages = clientA.getRecievedRequestedMessages();
            ChatMessageDTO lastRecievedRequestedMessage = requestedReceivedMessages.get(requestedReceivedMessages.size() - 1);
            assertEquals(clientA.getSentMessages().get(i).getText(), lastRecievedRequestedMessage.getText());
        }

        clientA.disconnect();
    }



    @Test
    public void requestNonExistentMessagesWithGreaterThanTimestamp() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP;
        clientA.requestMessages(0, requestType, 10, 0);
        assertThrows
        (
            ConditionTimeoutException.class, 
            () -> 
            {
                Awaitility.await().until(() -> clientA.getRecievedRequestedMessages().size() > 0);
            }
        );

        clientA.disconnect();
    }

    @Test
    public void checkThatOnlyRelevantClientRecievesRequestedMessage() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        ChatTestClient clientB = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        clientA.sendMessage("Hello from A");
        Awaitility.await().until(() -> clientA.getRecievedMessages().size() == 1);

        assertEquals(List.of(), clientA.getRecievedRequestedMessages());
        assertEquals(List.of(), clientB.getRecievedRequestedMessages());

        int requestedMessageCountLimit = 1;
        long oneHourInSeconds = 60 * 60;
        long currentUnixTimestamp = Instant.now().getEpochSecond() + oneHourInSeconds;
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.LESS_THAN_TIMESTAMP;
        clientB.requestMessages(currentUnixTimestamp, requestType, requestedMessageCountLimit, 0);
        Awaitility.await().until(() -> clientB.getRecievedRequestedMessages().size() > 0);

        assertEquals(List.of(), clientA.getRecievedRequestedMessages());
        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientB.getRecievedRequestedMessages());

        clientA.disconnect();
        clientB.disconnect();
    }

    @Test
    public void checkThatMessageCountLimitIsApplied() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            clientA.sendMessage("Hello from A from " + i);
            Integer objectI = i;
            Awaitility.await().until(() -> clientA.getRecievedMessages().size() == objectI + 1);

        }
        Awaitility.await().until(() -> clientA.getRecievedMessages().size() == messageCount);

        int messageCountLimit = 2;
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.GREATER_THAN_TIMESTAMP;
        clientA.requestMessages(0, requestType, messageCountLimit, 0);
        Awaitility.await().until(() -> clientA.getRecievedRequestedMessages().size() > 0);

        assertEquals(messageCountLimit, clientA.getRecievedRequestedMessages().size());

        clientA.disconnect();
    }

    @Test
    public void requestMessagesFromNonExistentChatRoom() throws Exception
    {
        String nonExistentRoomName = "NON EXISTENT ROOM NAME";
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);
        ChatTestClient clientB = new ChatTestClient(nonExistentRoomName, this.port, this.websocketURL);

        clientA.sendMessage("Hello from A");
        Awaitility.await().until(() -> clientA.getRecievedMessages().size() == 1);

        long currentUnixTimestamp = Instant.now().getEpochSecond();
        MessageRequestByTimestampType requestType = MessageRequestByTimestampType.LESS_THAN_TIMESTAMP;
        clientB.requestMessages(currentUnixTimestamp, requestType, 10, 0);
        assertThrows
        (
            ConditionTimeoutException.class, 
            () -> 
            {
                Awaitility.await().until(() -> clientB.getRecievedRequestedMessages().size() > 0);
            }
        );

        clientA.disconnect();
        clientB.disconnect();
    }

    @Test
    public void messageRequestLessThanId() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            clientA.sendMessage("Hello " + i);
            Integer objectI = i;
            Awaitility.await().until(() -> clientA.getRecievedMessages().size() == objectI + 1);

        }
        Awaitility.await().until(() -> clientA.getRecievedMessages().size() == messageCount);

        long id = messageCount + 1;
        MessageRequestByIDType requestType = MessageRequestByIDType.LESS_THAN_ID;
        clientA.requestMessages(id, requestType, messageCount);
        Awaitility.await().until(() -> clientA.getRecievedRequestedMessages().size() == messageCount);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());

        clientA.disconnect();
    }

    @Test
    public void messageRequestGreaterThanId() throws Exception
    {
        ChatTestClient clientA = new ChatTestClient(this.roomName, this.port, this.websocketURL);

        int messageCount = 3;
        for (int i = 0; i < messageCount; i++)
        {
            clientA.sendMessage("Hello " + i);
            Integer objectI = i;
            Awaitility.await().until(() -> clientA.getRecievedMessages().size() == objectI + 1);

        }
        Awaitility.await().until(() -> clientA.getRecievedMessages().size() == messageCount);

        long id = 0;
        MessageRequestByIDType requestType = MessageRequestByIDType.GREATER_THAN_ID;
        clientA.requestMessages(id, requestType, messageCount);
        Awaitility.await().until(() -> clientA.getRecievedRequestedMessages().size() == messageCount);

        assertSentMessagesEqualRequested(clientA.getSentMessages(), clientA.getRecievedRequestedMessages());

        clientA.disconnect();
    }
}