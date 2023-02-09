package com.example.web_chat;

class MessageRequest
{
    private int messageCount;

    public MessageRequest()
    {
    };

    public MessageRequest(int messageCount)
    {
        this.messageCount = messageCount;
    }

    public int getMessageCount()
    {
        return this.messageCount;
    }
}