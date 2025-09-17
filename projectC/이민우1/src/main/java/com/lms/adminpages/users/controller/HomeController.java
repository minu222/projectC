package com.lms.adminpages.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping( "/home")
    public String home(Model model) {
        // 필요한 데이터가 있다면 model.addAttribute()로 추가 가능
        model.addAttribute("title", "DW Academy Dashboard");
        return "adminpages/home"; // templates/home/index.html
    }
}