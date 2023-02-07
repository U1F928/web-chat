package com.example.web_chat;

public class UserMessage
{
    private String text;


    public UserMessage(){};

    public UserMessage(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}