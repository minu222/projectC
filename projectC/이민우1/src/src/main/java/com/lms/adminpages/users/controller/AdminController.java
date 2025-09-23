package com.lms.adminpages.users.controller;

import com.lms.adminpages.users.entity.User;
import com.lms.adminpages.users.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/instructors")
    public String instructorList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String keywordType,
            @RequestParam(required = false) String keyword,
            Model model) {

        // 필터 적용한 강사 리스트 조회
        List<User> instructors = userService.getInstructors(status, department, keywordType, keyword);

        // 모델에 추가
        model.addAttribute("instructors", instructors);
        model.addAttribute("statusFilter", status);
        model.addAttribute("departmentFilter", department);
        model.addAttribute("keywordType", keywordType);
        model.addAttribute("keyword", keyword);

        // templates/adminpages/instructor-info/index.html 경로
        return "adminpages/instructor-info/index";
    }

    @PostMapping("/instructors/delete")
    public String deleteInstructors(@RequestParam("ids") int[] ids) {
        userService.deleteUsers(ids);
        return "redirect:/admin/instructors";
    }


    @GetMapping("/students")
    public String studentList(@RequestParam(required = false) String status,
                              @RequestParam(required = false) String department,
                              @RequestParam(required = false) String keywordType,
                              @RequestParam(required = false) String keyword,
                              Model model) {

        List<User> students = userService.getStudents(status, department, keywordType, keyword);

        model.addAttribute("students", students);
        model.addAttribute("statusFilter", status);         // 상태 필터 선택 유지
        model.addAttribute("departmentFilter", department); // 소속 필터 선택 유지
        model.addAttribute("keywordType", keywordType);     // 검색 타입 선택 유지
        model.addAttribute("keyword", keyword);             // 검색어 유지

        return "adminpages/student-info/index"; // templates 폴더 내 강사 정보 페이지
    }

    @PostMapping("/students/delete")
    public String deleteStudents(@RequestParam("ids") int[] ids) {
        userService.deleteUsers(ids);
        return "redirect:/admin/students";
    }
}
