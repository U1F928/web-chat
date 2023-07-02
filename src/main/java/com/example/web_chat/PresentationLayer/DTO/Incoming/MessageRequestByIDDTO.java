package com.example.web_chat.PresentationLayer.DTO.Incoming;

public class MessageRequestByIDDTO
{
    private long id;

    private MessageRequestByIDType requestType;

    private int messageCountLimit;

    public MessageRequestByIDDTO(long id, MessageRequestByIDType requestType, int messageCountLimit)
    {
        this.id = id;
        this.requestType = requestType;
        this.messageCountLimit = messageCountLimit;
    }

    public long getID()
    {
        return this.id;
    }

    public int getMessageCountLimit()
    {
        return this.messageCountLimit;
    }

    public MessageRequestByIDType getRequestType()
    {
        return this.requestType;
    }

}
