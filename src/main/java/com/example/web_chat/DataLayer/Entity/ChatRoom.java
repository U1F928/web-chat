package com.example.web_chat.DataLayer.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom
{
    @NotNull
    @Id
    @Column(name = "chat_room_name", nullable = false)
    private String roomName;

    public ChatRoom()
    {
    };

    public ChatRoom(String roomName)
    {
        this.roomName = roomName;
    }

    public String getRoomName()
    {
        return this.roomName;
    }
}