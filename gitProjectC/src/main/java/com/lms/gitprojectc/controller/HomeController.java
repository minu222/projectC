package com.lms.gitprojectc.controller;

import com.lms.gitprojectc.service.HomeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        // 서비스에서 데이터 가져오기
        model.addAttribute("studentCount", homeService.getStudentCount());
        model.addAttribute("teacherCount", homeService.getTeacherCount());
        model.addAttribute("courseCount", homeService.getCourseCount());
        model.addAttribute("avgCompletion", homeService.getAvgCompletion());
        model.addAttribute("cancelRate", homeService.getCancelRate());
        model.addAttribute("monthRevenue", homeService.getMonthRevenue());
        model.addAttribute("notices", homeService.getRecentNotices());

        return "home"; // templates/home.html
    }
}