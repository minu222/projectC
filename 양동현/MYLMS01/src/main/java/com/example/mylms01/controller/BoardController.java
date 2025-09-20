package com.example.mylms01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class BoardController {

    @GetMapping("/main")
    public String boardMain(Model model) {
        model.addAttribute("bodyFragment", "board/main :: content");
        model.addAttribute("title", "게시판 메인");
        return "layout";
    }
}
