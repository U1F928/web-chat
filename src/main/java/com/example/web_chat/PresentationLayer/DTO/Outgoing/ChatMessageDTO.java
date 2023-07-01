package com.example.web_chat.PresentationLayer.DTO.Outgoing;

public class ChatMessageDTO
{
    private Long id;

    private String roomName;

    private long creationTimestamp;

    private String text;

    // JSON deserialization requires a default constructor to be present
    public ChatMessageDTO(){}

    public ChatMessageDTO(long id, String roomName, long unixTimestamp, String text)
    {
        this.id = id;
        this.roomName = roomName;
        this.creationTimestamp = unixTimestamp;
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

    public String getRoomName()
    {
        return this.roomName;
    }

    public long getCreationTimestamp()
    {
        return this.creationTimestamp;
    }
}