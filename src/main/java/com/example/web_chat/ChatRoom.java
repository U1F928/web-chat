package com.example.web_chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom
{
    @Id
    @Column(name = "chat_room_name")
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