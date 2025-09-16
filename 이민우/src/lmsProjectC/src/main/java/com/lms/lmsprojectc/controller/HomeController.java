package com.lms.lmsprojectc.controller;

import com.lms.lmsprojectc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("title", "DW Academy Home");
        return "home"; // → templates/home.html
    }

    @GetMapping("/admin/instructors")
    public String instructors(Model model) {
        model.addAttribute("instructor", userService.getAllInstructors());
        return "instructor-info/index";
    }


}