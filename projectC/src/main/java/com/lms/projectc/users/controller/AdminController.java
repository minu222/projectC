package com.lms.projectc.users.controller;

import com.lms.projectc.users.entity.User;
import com.lms.projectc.users.service.InstructorService;
import com.lms.projectc.users.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final InstructorService instructorService;
    private final StudentService studentService;

    public AdminController(InstructorService instructorService, StudentService studentService) {
        this.instructorService = instructorService;
        this.studentService = studentService;
    }

    @GetMapping("/instructors")
    public String instructors(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String keywordType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model) {

        List<User> instructors = instructorService.searchInstructors(department, keywordType, keyword, status);
        model.addAttribute("instructors", instructors);
        return "instructor-info/index";
    }

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "student-info/index";
    }


    @GetMapping("/info-trash")
    public String infoTrash() {
        return "info-trash/index";
    }

    @GetMapping("/member-details")
    public String memberDetails() {
        return "member-details/index";
    }
}