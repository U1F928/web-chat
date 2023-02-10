package com.example.web_chat.ChatRoom;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.details
 * 
 * The repository proxy has two ways to derive a store-specific query from the method name:
 *  - By deriving the query from the method name directly.
 *  - By using a manually defined query.
 * 
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String>
{
    ChatRoom findByRoomName(String roomName);
}
