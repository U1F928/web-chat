package com.example.web_chat.DataLayer.Repository;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.web_chat.DataLayer.Entity.ChatMessage;
import com.example.web_chat.DataLayer.Entity.ChatRoom;

/*
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.details
 * 
 * The repository proxy has two ways to derive a store-specific query from the method name:
 *  - By deriving the query from the method name directly.
 *  - By using a manually defined query.
 * 
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>
{
    @Query("SELECT c FROM ChatMessage c WHERE c.room = :room AND c.creationTimestamp <= :creationTimestamp ORDER BY c.creationTimestamp DESC, c.id DESC")
    ArrayList<ChatMessage> findByRoomAndUnixTimestampLessThan(ChatRoom room, long creationTimestamp, Pageable pageable);

    @Query("SELECT c FROM ChatMessage c WHERE c.room = :room AND c.creationTimestamp >= :creationTimestamp ORDER BY c.creationTimestamp ASC, c.id ASC")
    ArrayList<ChatMessage> findByRoomAndUnixTimestampGreaterThan(ChatRoom room, long creationTimestamp, Pageable pageable);
}
