package com.example.web_chat.DataLayer.Repository;

import java.util.List;

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
    @Query("SELECT c FROM ChatMessage c WHERE c.room = :room AND c.creationTimestamp < :creationTimestamp ORDER BY c.creationTimestamp DESC, c.id DESC")
    List<ChatMessage> findByRoomAndCreationTimestampLessThan(ChatRoom room, long creationTimestamp, Pageable pageable);

    @Query("SELECT c FROM ChatMessage c WHERE c.room = :room AND c.creationTimestamp > :creationTimestamp ORDER BY c.creationTimestamp ASC, c.id ASC")
    List<ChatMessage> findByRoomAndCreationTimestampGreaterThan(ChatRoom room, long creationTimestamp, Pageable pageable);

    @Query("SELECT c FROM ChatMessage c WHERE c.room = :room AND c.id < :id ORDER BY c.id DESC")
    List<ChatMessage> findByRoomAndIDLessThan(ChatRoom room, long id, Pageable pageable);

    @Query("SELECT c FROM ChatMessage c WHERE c.room = :room AND c.id > :id ORDER BY c.id ASC")
    List<ChatMessage> findByRoomAndIDGreaterThan(ChatRoom room, long id, Pageable pageable);

}
