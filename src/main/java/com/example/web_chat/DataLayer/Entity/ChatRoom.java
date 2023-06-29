package com.example.web_chat.DataLayer.Entity;

import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom
{
    @NonNull
    @Id
    @Column(name = "chat_room_name", nullable = false)
    private String roomName;

    public ChatRoom(){};

    public ChatRoom(String roomName)
    {
        this.roomName = roomName;
    }

    public String getRoomName()
    {
        return this.roomName;
    }
}