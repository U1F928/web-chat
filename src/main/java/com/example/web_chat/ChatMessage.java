package com.example.web_chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "chat_room_name")
    private ChatRoom room;

    @Column(name = "chat_message_unix_timestamp")
    private long unixTimestamp;

    @Column(name = "chat_message_text")
    private String text;

    // JSON deserialization requires a default constructor to be present
    public ChatMessage(){}

    public ChatMessage(ChatRoom room, long unixTimestamp, String text)
    {
        this.room= room;
        this.unixTimestamp = unixTimestamp;
        this.text = text;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String newText)
    {
        this.text = newText;
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
