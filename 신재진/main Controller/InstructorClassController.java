package com.lms.mainpages.users.controller;

import com.lms.mainpages.users.entity.User;
import com.lms.mainpages.users.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("instructor/myclass")
public class InstructorClassController {
    private final UserService userService;

    public InstructorClassController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public String classlist(){

        return "mainpages/instructor/myclass/classlist/main";
    }


    @GetMapping("/payments")
    public String classpayments(){

        return "adminpages/instructor/myclass/payments/main";
    }

    @GetMapping("/email")
    public String email(){

        return "adminpages/instructor/myclass/email/main";
    }

    @GetMapping("/noticeboard")
    public String noticeboard(){

        return "adminpages/instructor/myclass/noticeboard/main";
    }



    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        model.addAttribute("user", loginUser);
        if (loginUser.getRole() != null && "INSTRUCTOR".equalsIgnoreCase(loginUser.getRole().toString())) {
            model.addAttribute("instructorProfile",
                    userService.findInstructorProfile(loginUser.getUser_id()).orElse(null));
        }
        return "mainpages/edit/index";
    }

    @PostMapping("/update") // ← /profile/update
    public String updateProfile(@ModelAttribute User user,
                                @RequestParam(required=false) String affiliation,
                                @RequestParam(required=false) String bio,
                                HttpSession session,
                                RedirectAttributes ra) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            ra.addFlashAttribute("errorMessage","다시 로그인 해주세요.");
            return "redirect:/user/login";
        }

        // 폼의 user_id는 무시하고 세션 PK로 고정
        user.setUser_id(loginUser.getUser_id());

        userService.updateProfile(user, affiliation, bio);

        // 세션 최신화 (선택)
        userService.findById(loginUser.getUser_id())
                .ifPresent(u -> session.setAttribute("loginUser", u));

        ra.addFlashAttribute("successMessage","프로필이 저장되었습니다.");
        return "redirect:/profile"; // ← GET "" 으로 렌더
    }
}
