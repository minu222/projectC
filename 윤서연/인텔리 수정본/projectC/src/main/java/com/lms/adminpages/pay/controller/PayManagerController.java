package com.lms.adminpages.pay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
public class PayManagerController {

    @GetMapping("/payment-management")
    public String payManager(){
        return "adminpages/payment-management/index";
    }
}
