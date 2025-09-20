package com.example.mylms01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/myclass")
public class MyClassController {

    @GetMapping("/main")
    public String myclassMain(Model model) {
        model.addAttribute("bodyFragment", "myclass/main :: content");
        model.addAttribute("title", "나의 강의실 메인");
        return "layout";
    }
}
