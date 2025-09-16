package com.lms.lmsprojectc.controller;

import com.lms.lmsprojectc.entity.User;
import com.lms.lmsprojectc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    // 홈 화면
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("title", "DW Academy Home");
        return "home"; // templates/home.html
    }

    // 강사 목록 조회 (검색 + 상태 필터)
    @GetMapping("/admin/instructors")
    public String instructors(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            Model model) {

        // 서비스에 필터 인자를 넘겨서 조회
        List<User> instructors = userService.getAllInstructors(null, type, keyword, status);

        model.addAttribute("instructors", instructors);
        return "instructor-info/index"; // templates/instructor-info/index.html
    }

    // 선택 탈퇴
    @PostMapping("/admin/instructors/delete")
    public String deleteInstructors(@RequestParam(name = "ids") List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            userService.deleteInstructors(ids);
        }
        return "redirect:/admin/instructors";
    }
}
