package com.example.web_chat.ChatMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.springframework.lang.NonNull;

import com.example.web_chat.ChatRoom.ChatRoom;

@Entity @Table(name = "chat_messages")
public class ChatMessage
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "chat_message_id")
    private Long id;

    @NonNull
    @OneToOne @JoinColumn(name = "chat_room_name", nullable = false)
    private ChatRoom room;

    @Column(name = "chat_message_unix_timestamp", nullable = false)
    private long unixTimestamp;

    @NonNull
    @Column(name = "chat_message_text", nullable = false)
    private String text;

    public ChatMessage(){}
    public ChatMessage(ChatRoom room, long unixTimestamp, String text)
    {
        this.room = room;
        this.unixTimestamp = unixTimestamp;
        this.text = text;
    }

    public String getText()
    {
        return this.text;
    }

    public ChatRoom getRoom()
    {
        return this.room;
    }

    public long getUnixTimestamp()
    {
        return this.unixTimestamp;
    }

}
