package com.example.web_chat.PresentationLayer.DTO.Incoming;

public class MessageRequestDTO
{

    private long unixTimestamp;

    private MessageRequestType requestType;

    private int messageCount;

    public MessageRequestDTO(long unixTimestamp, MessageRequestType requestType, int messageCount)
    {
        this.unixTimestamp = unixTimestamp;
        this.requestType = requestType;
        this.messageCount = messageCount;
    };

    public long getUnixTimestamp()
    {
        return this.unixTimestamp;
    }

    public MessageRequestType getRequestType()
    {
        return this.requestType;
    }

    public int getMessageCount()
    {
        return this.messageCount;
    }
}