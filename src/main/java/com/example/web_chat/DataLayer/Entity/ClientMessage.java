package com.example.web_chat.DataLayer.Entity;

public class ClientMessage
{
    private String text;

    // JSON deserialization requires a default constructor to be present
    public ClientMessage(){};

    public ClientMessage(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return this.text;
    }
}