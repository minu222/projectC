// src/main/java/dwacademy/mylms001/controller/InstructorCourseRegController.java
package com.lms.mainpages.controller;

import com.lms.mainpages.entity.User;
import com.lms.mainpages.service.CourseService;
import com.lms.mainpages.web.CourseForm;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class InstructorCourseRegController {

    private final CourseService courseService;

    /**
     * 강의 등록 처리 (POST) - 성공 시 /myclass/teacher/register 로 리다이렉트
     * GET("/myclass/teacher/register")는 MyClassController에서 담당합니다.
     */
    @PostMapping(value = "/instructor/courses/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Map<String,Object> addCourse(
            @RequestParam int instructorId,
            @ModelAttribute CourseForm form,
            @RequestParam("mainImage") MultipartFile mainImage,
            @RequestParam("examFile") MultipartFile examFile
    ) throws IOException {
        long courseId = courseService.createCourse(instructorId, form, mainImage, examFile);
        return Map.of("courseId", courseId); // JSON 반환
    }

    public String addCourse(
            @ModelAttribute("form") CourseForm form,
            @RequestParam(name = "mainImage", required = false) MultipartFile mainImage,
            @RequestParam(name = "examFile",  required = false) MultipartFile examFile,
            @RequestParam(name = "videoFile", required = false) MultipartFile videoFile,
            HttpSession session,
            RedirectAttributes ra
    ) {
        try {
            User login = (User) session.getAttribute("loginUser");
            if (login == null) {
                ra.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
                return "redirect:/login";
            }
            int instructorId = login.getUser_id();

            long courseId = courseService.createCourse(instructorId, form, mainImage, examFile);

            // VOD 선택 시 동영상 업로드가 있다면 course_materials 테이블에 저장
            courseService.saveVideoMaterial(courseId, videoFile);

            System.out.println(">>>>>>>>>> 여기까지 ok");

            ra.addFlashAttribute("toast", "강의 등록이 완료되었습니다.");
            return "redirect:/myclass/teacher/register";
        } catch (Exception e) {
            log.error("Course add failed", e);
            ra.addFlashAttribute("errorMessage", "등록 중 오류가 발생했습니다: " + e.getMessage());
            ra.addFlashAttribute("form", form);
            return "redirect:/myclass/teacher/register";
        }




    }
}
