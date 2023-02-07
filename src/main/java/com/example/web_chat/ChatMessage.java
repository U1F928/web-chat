package com.example.web_chat;

public class ChatMessage
{
    private String roomName;
    private int inRoomID;
    private String text;

    public ChatMessage(){}

    public ChatMessage(String text)
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
