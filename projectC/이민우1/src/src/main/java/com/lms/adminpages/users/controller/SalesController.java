package com.lms.adminpages.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sales")
public class SalesController {


    @GetMapping("/annual-revenue-stats")
    public String annualrevenuestats(){

        return "adminpages/annual-revenue-stats/index";
    }


    @GetMapping("/monthly-revenue-stats")
    public String monthlyrevenuestats(){

        return "adminpages/monthly-revenue-stats/index";
    }

    @GetMapping("/course-revenue-stats")
    public String courserevenuestats(){

        return "adminpages/course-revenue-stats/index";
    }

    @GetMapping("/tax-management")
    public String taxmanagement(){

        return "adminpages/tax-management/index";
    }
}