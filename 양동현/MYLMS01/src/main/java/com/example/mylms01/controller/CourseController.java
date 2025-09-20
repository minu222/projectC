package com.example.mylms01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/course")
public class CourseController {

    @GetMapping("/main")
    public String courseMain(Model model) {
        model.addAttribute("bodyFragment", "course/main :: content");
        model.addAttribute("title", "교육과정 메인");
        return "layout";
    }
}
