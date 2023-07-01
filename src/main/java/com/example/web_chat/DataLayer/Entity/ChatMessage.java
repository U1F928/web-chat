package com.example.web_chat.DataLayer.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity @Table(name = "chat_messages")
public class ChatMessage
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "chat_message_id")
    private Long id;

    @NotNull
    @ManyToOne @JoinColumn(name = "chat_room_name", nullable = false)
    private ChatRoom room;

    @Column(name = "chat_message_creation_timestamp", nullable = false)
    private long creationTimestamp;

    @NotNull
    @Column(name = "chat_message_text", nullable = false)
    private String text;

    // JSON deserialization requires a default constructor to be present
    public ChatMessage(){}

    public ChatMessage(ChatRoom room, long creationTimestamp, String text)
    {
        this.room = room;
        this.creationTimestamp = creationTimestamp;
        this.text = text;
    }

    public long getID()
    {
        return this.id;
    }

    public String getText()
    {
        return this.text;
    }

    public ChatRoom getRoom()
    {
        return this.room;
    }

    public long getCreationTimestamp()
    {
        return this.creationTimestamp;
    }
}
