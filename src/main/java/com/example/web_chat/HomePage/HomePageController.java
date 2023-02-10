package com.example.web_chat.HomePage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HomePageController
{

    @GetMapping("/")
    public String greeting()
    {
        return "home.html";
    }
}