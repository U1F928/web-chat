package com.example.web_chat.PresentationLayer.DTO.Incoming;

public class MessageRequestByTimestampDTO
{

    private long creationTimestamp;

    private MessageRequestByTimestampType requestType;

    private int messageCountLimit;

    public MessageRequestByTimestampDTO(long creationTimestamp, MessageRequestByTimestampType requestType, int messageCountLimit)
    {
        this.creationTimestamp = creationTimestamp;
        this.requestType = requestType;
        this.messageCountLimit = messageCountLimit;
    };

    public long getCreationTimestamp()
    {
        return this.creationTimestamp;
    }

    public MessageRequestByTimestampType getRequestType()
    {
        return this.requestType;
    }

    public int getMessageCountLimit()
    {
        return this.messageCountLimit;
    }
}