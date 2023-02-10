package com.example.web_chat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String>
{
    ChatRoom findByRoomName(String roomName);
}
