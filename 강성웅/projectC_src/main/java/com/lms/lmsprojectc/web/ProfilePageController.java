package com.lms.lmsprojectc.web;

import com.lms.lmsprojectc.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProfilePageController {

    @GetMapping("/account/profile") // <- /profile와 겹치지 않도록 변경
    public String profile(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        model.addAttribute("unreadCount", 0);
        model.addAttribute("user", loginUser);
        return "edit";
    }
}
