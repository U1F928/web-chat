package com.example.web_chat.PresentationLayer.Controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration @EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer
{

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        registry.setApplicationDestinationPrefixes("/app");
        String messageBrokerURL = System.getenv("MESSAGE_BROKER_URL");
        int messageBrokerPort = Integer.parseInt(System.getenv("MESSAGE_BROKER_PORT"));
        String messageBrokerUsername = System.getenv("MESSAGE_BROKER_USERNAME");
        String messageBrokerPassword = System.getenv("MESSAGE_BROKER_PASSWORD");
        //https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/messaging/simp/config/StompBrokerRelayRegistration.html
        StompBrokerRelayRegistration relay = registry.enableStompBrokerRelay("/topic");
        relay.setRelayHost(messageBrokerURL);
        relay.setRelayPort(messageBrokerPort);
        /* 
           Set the passcode for the shared "system" connection used to send 
           messages to the STOMP broker from within the application, 
           i.e. messages not associated with a specific client session
        */
        relay.setSystemLogin(messageBrokerUsername);
        relay.setSystemPasscode(messageBrokerPassword);

        /* 
            Set the login/passcode to use when creating connections to the STOMP 
            broker on behalf of connected clients.
        */
        relay.setClientLogin(messageBrokerUsername);
        relay.setClientPasscode(messageBrokerPassword);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        // Use ws://localhost:8080/websocket to establish websocket connection
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*");
        // Use http://localhost:8080/websocket to establish websocket connection
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*").withSockJS();
    }
}