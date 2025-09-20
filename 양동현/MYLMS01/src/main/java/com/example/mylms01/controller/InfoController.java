package com.example.mylms01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/info")
public class InfoController {

    @GetMapping("/main")
    public String infoMain(Model model) {
        model.addAttribute("bodyFragment", "info/main :: content");
        model.addAttribute("title", "정보센터 메인");
        return "layout";
    }
}
