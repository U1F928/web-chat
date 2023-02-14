package com.example.web_chat.ChatTestClient;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.example.web_chat.ChatMessage.ChatMessage;
import com.example.web_chat.ClientMessage.ClientMessage;

public class ChatTestClient
{
    private String webSocketURL;

    private int port;

    private SockJsClient sockJsClient;

    private WebSocketStompClient stompClient;

    private StompSessionHandler sessionHandler;

    private StompSession session;

    public String roomName;

    private ArrayList<ChatMessage> recievedMessages;

    public ChatTestClient(String roomName, int port, String webSocketURL) throws Exception
    {
        this.webSocketURL = webSocketURL;
        this.recievedMessages = new ArrayList<ChatMessage>();
        this.port = port;
        this.setupStompClient();

        final CountDownLatch latch = new CountDownLatch(1);
        this.setupSessionHandler(latch);
        this.stompClient.connectAsync(this.webSocketURL, this.sessionHandler, this.port);
        if (!latch.await(3, TimeUnit.SECONDS))
        {
            fail("Failed to set up the session handler.");
        }
    }

    public void sendMessage(String roomName, ClientMessage clientMessage)
    {
        this.session.send("/app/room/" + this.roomName + "/publish_message", clientMessage);
    }

    public ArrayList<ChatMessage> getRecievedMessages()
    {
        return this.recievedMessages;
    }

    private void setupStompClient()
    {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.sockJsClient = new SockJsClient(transports);
        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    private void setupSessionHandler(CountDownLatch latch)
    {
        String roomName = this.roomName;
        ArrayList<ChatMessage> recievedMessages = this.recievedMessages;
        ChatTestClient client = this;
        this.sessionHandler = new TestSessionHandler()
        {
            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders)
            {
                client.session = session;
                session.subscribe("/topic/room/" + roomName, new StompFrameHandler()
                {
                    @Override
                    public Type getPayloadType(StompHeaders headers)
                    {
                        return ChatMessage.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload)
                    {
                        ChatMessage chatMessage = (ChatMessage) payload;
                        recievedMessages.add(chatMessage);
                    }
                });
                latch.countDown();
            }
        };
    }
}