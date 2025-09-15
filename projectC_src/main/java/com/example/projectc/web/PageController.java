package com.example.projectc.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "index";      // templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login"; // templates/auth/login.html
    }

    @GetMapping("/signup")
    public String signup() {
        return "auth/signup"; // templates/auth/signup.html
    }
}
