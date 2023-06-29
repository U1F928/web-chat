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
    /*
     * TODO: To prevent users from having access to sequential IDs, add a random UUID field 
     * that can be used when interacting with them.
     */

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "chat_message_id")
    private Long id;

    @NotNull
    @ManyToOne @JoinColumn(name = "chat_room_name", nullable = false)
    private ChatRoom room;

    @Column(name = "chat_message_unix_timestamp", nullable = false)
    private long unixTimestamp;

    @NotNull
    @Column(name = "chat_message_text", nullable = false)
    private String text;

    // JSON deserialization requires a default constructor to be present
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
