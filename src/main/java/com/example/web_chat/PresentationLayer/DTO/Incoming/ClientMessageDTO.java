package com.example.web_chat.PresentationLayer.DTO.Incoming;

public class ClientMessageDTO
{
    private String text;

    // JSON deserialization requires a default constructor to be present
    public ClientMessageDTO(){};

    public ClientMessageDTO(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return this.text;
    }
}