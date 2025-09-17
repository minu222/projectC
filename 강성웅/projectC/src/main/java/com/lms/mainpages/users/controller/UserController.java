package com.lms.mainpages.users.controller;

import com.lms.mainpages.users.entity.User;
import com.lms.mainpages.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UserController {


    //private UserRepository repo;
    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/user/add")
    public String addForm(Model model){
        model.addAttribute("user",new User());
        return "mainpages/users/addUser";
    }

    //User 등록
    @PostMapping("/user/add")
    public String addUser(@ModelAttribute User user,
                          @RequestParam(required=false) String affiliation,
                          @RequestParam(required=false) String bio,
                          RedirectAttributes ra) {
        try {
            // role은 INSTRUCTOR/STUDENT 로 넘어오도록 폼/JS 맞추기
            userService.joinWithInstructorProfile(user, affiliation, bio);
            ra.addFlashAttribute("successMessage","회원가입이 완료되었습니다. 로그인 해주세요.");
            return "redirect:/user/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/add";
        }
    }


    //로그인 폼으로 이동
    @GetMapping({"/user/login", "/login"})
    public String loginForm(){
        return "login";
    }

    @PostMapping("/user/login")
    public String login(@RequestParam("nickname") String nickname,
                        @RequestParam("password") String password,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes){
        Optional<User> loginResult = userService.login(nickname, password);

        if(loginResult.isPresent()){
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", loginResult.get());
            return "index";
        }else {
            redirectAttributes.addFlashAttribute("errorMessage", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "redirect:/user/login";
        }

    }

    @GetMapping("/user/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session != null){
            session.invalidate();
        }
        return "redirect:/";
    }



    @GetMapping("/user/list")
    public String userlist(Model model){
        System.out.println("userlist");
        model.addAttribute("users", userService.findAll());
        return "users/userlist";
    }

    @GetMapping("/user/edit/{id}")
    public String userEditForm(@PathVariable String id, Model model){
        User user = (User) userService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        model.addAttribute("user", user);
        System.out.println("회원:"+user);

        return "users/editUser";
    }

    @PostMapping("/user/edit")
    public String userEdit(@ModelAttribute User user){
        userService.update(user);
        return "redirect:/user/list";
    }

    @PostMapping("/user/delete")
    public String deleteUser(@RequestParam String id){
        userService.deleteById(id);
        return "redirect:/user/list";
    }


}
