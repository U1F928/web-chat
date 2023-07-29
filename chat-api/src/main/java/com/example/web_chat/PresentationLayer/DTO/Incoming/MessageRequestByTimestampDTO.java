package com.example.web_chat.PresentationLayer.DTO.Incoming;

public class MessageRequestByTimestampDTO
{

    private long creationTimestamp;

    private MessageRequestByTimestampType requestType;

    private int pageSize;
    
    private int pageNumber;

    public MessageRequestByTimestampDTO(long creationTimestamp, MessageRequestByTimestampType requestType, int pageSize, int pageNumber)
    {
        this.creationTimestamp = creationTimestamp;
        this.requestType = requestType;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    };

    public long getCreationTimestamp()
    {
        return this.creationTimestamp;
    }

    public MessageRequestByTimestampType getRequestType()
    {
        return this.requestType;
    }

    public int getPageSize()
    {
        return this.pageSize;
    }

    public int getPageNumber()
    {
        return this.pageNumber;
    }
}