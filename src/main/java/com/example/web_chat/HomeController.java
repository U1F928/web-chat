package com.example.web_chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController
{

    @GetMapping("/")
    public String greeting()
    {
        return "home.html";
    }
}