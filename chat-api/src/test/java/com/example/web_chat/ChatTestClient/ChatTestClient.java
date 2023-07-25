package com.example.web_chat.ChatTestClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.example.web_chat.PresentationLayer.DTO.Incoming.ClientMessageDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByIDDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByIDType;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByTimestampDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestByTimestampType;
import com.example.web_chat.PresentationLayer.DTO.Outgoing.ChatMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatTestClient
{
    private StompSession stompSession;

    public String roomName;

    private ArrayList<ClientMessageDTO> sentMessages;

    private ArrayList<ChatMessageDTO> recievedMessages;

    private ArrayList<ChatMessageDTO> recievedRequestedMessages;

    public ChatTestClient(String roomName, int port, String webSocketURL) throws Exception
    {
        this.roomName = roomName;
        this.sentMessages = new ArrayList<ClientMessageDTO>();
        this.recievedMessages = new ArrayList<ChatMessageDTO>();
        this.recievedRequestedMessages = new ArrayList<ChatMessageDTO>();
        this.connect(webSocketURL, port);
        this.subscribeToRequestedMessages();
        this.subscribeToRoom(roomName);
    }

    // avoid AbortedException: io.netty.channel.StacklessClosedChannelException
    // https://github.com/reactor/reactor-netty/issues/1870
    public void disconnect()
    {
        this.stompSession.disconnect(null);
    }

    private WebSocketStompClient createStompClient()
    {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        ThreadPoolTaskScheduler messageBrokerTaskScheduler = new ThreadPoolTaskScheduler();
        messageBrokerTaskScheduler.setPoolSize(3);
        messageBrokerTaskScheduler.initialize();
        stompClient.setTaskScheduler(messageBrokerTaskScheduler);
        stompClient.setDefaultHeartbeat(new long[] { 0, 0 });
        return stompClient;
    }

    public void connect(String webSocketURL, int port) throws Exception
    {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        StompSessionHandler stompSessionHandler = new TestSessionHandler()
        {
            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders)
            {
                countDownLatch.countDown();
            }
        };

        WebSocketStompClient stompClient = this.createStompClient();
        CompletableFuture<StompSession> futureStompSession = stompClient.connectAsync(webSocketURL, stompSessionHandler,
                port);

        if (!countDownLatch.await(5, TimeUnit.SECONDS))
        {
            throw new Exception("Failed to connect");
        }
        this.stompSession = futureStompSession.get();
    }

    public Subscription subscribe(String destination, StompFrameHandler stompFrameHandler) throws Exception
    {
        CountDownLatch latch = new CountDownLatch(1);
        this.stompSession.setAutoReceipt(true);
        Subscription subscription = this.stompSession.subscribe(destination, stompFrameHandler);
        subscription.addReceiptTask(latch::countDown);
        if (!latch.await(5, TimeUnit.SECONDS))
        {
            throw new Exception("Failed to subscribe");
        }
        this.stompSession.setAutoReceipt(false);
        return subscription;
    }

    public Subscription subscribeToRoom(String roomName) throws Exception
    {
        StompFrameHandler stompFrameHandler = new StompFrameHandler()
        {
            @Override
            public Type getPayloadType(StompHeaders headers)
            {
                return ChatMessageDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload)
            {
                ChatMessageDTO chatMessage = (ChatMessageDTO) payload;
                recievedMessages.add(chatMessage);
            }
        };

        String destination = "/topic/room." + roomName;
        Subscription subscription = this.subscribe(destination, stompFrameHandler);
        return subscription;
    }

    public Subscription subscribeToRequestedMessages() throws Exception
    {
        StompFrameHandler stompFrameHandler = new StompFrameHandler()
        {
            @Override
            public Type getPayloadType(StompHeaders headers)
            {
                return List.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload)
            {
                List<Map<String, Object>> requestedMessages = (List<Map<String, Object>>) payload;
                ObjectMapper objectMapper = new ObjectMapper();
                for (Map<String, Object> messageMap : requestedMessages)
                {
                    ChatMessageDTO chatMessage = objectMapper.convertValue(messageMap, ChatMessageDTO.class);
                    recievedRequestedMessages.add(chatMessage);
                }
            }
        };

        String destination = "/user/topic/requested_messages";
        Subscription subscription = this.subscribe(destination, stompFrameHandler);
        return subscription;
    }

    public void sendMessage(ClientMessageDTO clientMessage)
    {
        String url = "/app/room/" + this.roomName + "/publish_message";
        this.stompSession.send(url, clientMessage);
        this.sentMessages.add(clientMessage);
    }

    public void sendMessage(String text)
    {
        ClientMessageDTO clientMessage = new ClientMessageDTO(text);
        this.sendMessage(clientMessage);
    }

    public void requestMessages(MessageRequestByTimestampDTO messageRequest)
    {
        String url = "/app/room/" + this.roomName + "/request_messages_by_timestamp";
        this.stompSession.send(url, messageRequest);
    }

    public void requestMessages(long creationTimestamp, MessageRequestByTimestampType messageRequestType,
            int pageSize, int pageNumber)
    {
        MessageRequestByTimestampDTO messageRequest = new MessageRequestByTimestampDTO(creationTimestamp,
                messageRequestType, pageSize, pageNumber);
        this.requestMessages(messageRequest);
    }

    public void requestMessages(MessageRequestByIDDTO messageRequest)
    {
        String url = "/app/room/" + this.roomName + "/request_messages_by_id";
        this.stompSession.send(url, messageRequest);
    }

    public void requestMessages(long id, MessageRequestByIDType messageRequestType, int messageCountLimit)
    {
        MessageRequestByIDDTO messageRequest = new MessageRequestByIDDTO(id, messageRequestType, messageCountLimit);
        this.requestMessages(messageRequest);
    }

    public ArrayList<ClientMessageDTO> getSentMessages()
    {
        return this.sentMessages;
    }

    public ArrayList<ChatMessageDTO> getRecievedMessages()
    {
        return this.recievedMessages;
    }

    public ArrayList<ChatMessageDTO> getRecievedRequestedMessages()
    {
        return this.recievedRequestedMessages;
    }

}