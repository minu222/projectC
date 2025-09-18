package com.lms.mainpages.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/info")
public class InfoController {


    @GetMapping("/introduction")
    public String introduction(){

        return "adminpages/board/introduction/index";
    }


    @GetMapping("/book")
    public String book(){

        return "adminpages/board/book/index";
    }

    @GetMapping("/examDate")
    public String examDate(){

        return "adminpages/board/examDate/index";
    }

    @GetMapping("/dataroom")
    public String dataroom(){

        return "adminpages/board/dataroom/index";
    }

}