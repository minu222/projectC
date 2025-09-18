package com.lms.mainpages.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/board")
public class BoardController {


    @GetMapping("/NoticesEvents")
    public String NoticesEvents(){

        return "adminpages/board/NoticesEvents/index";
    }


    @GetMapping("/faq")
    public String faq(){

        return "adminpages/board/faq/index";
    }

    @GetMapping("/free")
    public String free(){

        return "adminpages/board/free/index";
    }

    @GetMapping("/instructorreviews")
    public String instructorreviews(){

        return "adminpages/board/instructorreviews/index";
    }

    @GetMapping("/classreviews")
    public String classreviews(){

        return "adminpages/board/classreviews/index";
    }
}