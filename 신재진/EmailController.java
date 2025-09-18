package com.lms.adminpages.users.controller;


import com.lms.adminpages.users.entity.User;
import com.lms.adminpages.users.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/email")
public class EmailController {


    @GetMapping("/compose-instructor-email")
    public String email(){

        return "adminpages/compose-instructor-email/index";
    }


    @GetMapping("/email-send-history")
    public String emailList(){

        return "adminpages/email-send-history/index";
    }
}

