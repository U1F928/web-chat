package com.example.web_chat.ChatTestClient;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

import com.example.web_chat.PresentationLayer.DTO.Incoming.ClientMessageDTO;
import com.example.web_chat.PresentationLayer.DTO.Incoming.MessageRequestDTO;
import com.example.web_chat.PresentationLayer.DTO.Outgoing.ChatMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatTestClient
{
    private String webSocketURL;

    private int port;

    private SockJsClient sockJsClient;

    private WebSocketStompClient stompClient;

    private StompSessionHandler sessionHandler;

    private StompSession session;

    public String roomName;

    private ArrayList<ClientMessageDTO> sentMessages;

    private ArrayList<ChatMessageDTO> recievedMessages;

    private ArrayList<ChatMessageDTO> recievedRequestedMessages;

    public ChatTestClient(String roomName, int port, String webSocketURL) throws Exception
    {
        this.webSocketURL = webSocketURL;
        this.roomName = roomName;
        this.sentMessages = new ArrayList<ClientMessageDTO>();
        this.recievedMessages = new ArrayList<ChatMessageDTO>();
        this.recievedRequestedMessages = new ArrayList<ChatMessageDTO>();
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

    public void sendMessage(ClientMessageDTO clientMessage)
    {
        this.session.send("/app/room/" + this.roomName + "/publish_message", clientMessage);
        this.sentMessages.add(clientMessage);
    }

    public void requestMessages(MessageRequestDTO messageRequest)
    {
        this.session.send("/app/room/" + this.roomName + "/request_messages", messageRequest);
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
        ArrayList<ChatMessageDTO> recievedRequestedMessages = this.recievedRequestedMessages;
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
                        return ChatMessageDTO.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload)
                    {
                        ChatMessageDTO chatMessage = (ChatMessageDTO) payload;
                        recievedMessages.add(chatMessage);
                    }
                });
                session.subscribe("/user/topic/requested_messages", new StompFrameHandler()
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
                });

                latch.countDown();
            }
        };
    }
}