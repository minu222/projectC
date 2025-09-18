package com.lms.mainpages.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/curriculum")
public class curriculumController {


    @GetMapping("/courselist")
    public String courselist(){

        return "adminpages/curriculum/courselist/index";
    }


    @GetMapping("/traininglectures")
    public String traininglectures(){

        return "adminpages/curriculum/traininglectures/index";
    }


}