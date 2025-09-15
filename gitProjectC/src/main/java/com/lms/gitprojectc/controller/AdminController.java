package com.lms.gitprojectc.controller;

import com.lms.gitprojectc.entity.User;
import com.lms.gitprojectc.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // 강사 목록 페이지
    @GetMapping("/admin/instructors")
    public String instructorList(Model model) {
        List<User> instructors = userService.getAllInstructors(); // role == instructor
        model.addAttribute("instructors", instructors);
        return "information/instructor_info"; // templates/information/instructor_info.html
    }

    @GetMapping("/admin/students")
    public String studentList(Model model) {
        List<User> students = userService.getAllStudents(); // role == STUDENT
        model.addAttribute("students", students);
        return "information/student_info"; // templates/information/student_info.html
    }


    // 강사 상세 페이지
    @GetMapping("/admin/instructors/{id}")
    public String instructorDetail(@PathVariable Long id, Model model) {
        User instructor = userService.getInstructorById(id);
        model.addAttribute("instructor", instructor);
        return "infomation/instructor_detail"; // templates/information/instructor_detail.html
    }
}