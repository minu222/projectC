package com.lms.adminpages.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/order")
public class OrderController {


    @GetMapping("/order-list")
    public String orderlist(){

        return "adminpages/order-list/index";
    }


    @GetMapping("/payment-management")
    public String paymentmanagement(){

        return "adminpages/payment-management/index";
    }

    @GetMapping("/coupon-management")
    public String couponmanagement(){

        return "adminpages/coupon-management/index";
    }
}