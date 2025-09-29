package com.lms.adminpages.classrooms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class ClassroomStudentController {


    @GetMapping("/classroom-student-info")
    public String StudentInClassroom() {
        return "adminpages/classroom-student-info/index";
    }
}
