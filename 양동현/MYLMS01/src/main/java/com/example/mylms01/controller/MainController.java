package com.example.mylms01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // 홈페이지
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("bodyFragment", "index :: indexContent"); // 여기를 바꾸면 다른 페이지 삽입 가능
        model.addAttribute("title", "Welcome to MyLMS");             // 페이지 제목
        model.addAttribute("showSidebar", false);                   // 사이드바 표시 여부
        return "layout";
    }

    // 메인 페이지
    @GetMapping("/main")
    public String mainPage(Model model) {
        model.addAttribute("bodyFragment", "index :: indexContent"); // 여기를 바꾸면 다른 페이지 삽입 가능
        model.addAttribute("title", "My LMS Home");                 // 페이지 제목
        model.addAttribute("showSidebar", true);                   // 사이드바 표시 여부
        return "layout";
    }
    // 카테고리 메인 페이지
    @GetMapping("/category/main")
    public String categoryMain(Model model) {
        model.addAttribute("bodyFragment", "category/main :: categoryContent");
        model.addAttribute("title", "카테고리 메인");
        model.addAttribute("showSidebar", true); // 사이드바 켜기
        return "layout";
    }

}
