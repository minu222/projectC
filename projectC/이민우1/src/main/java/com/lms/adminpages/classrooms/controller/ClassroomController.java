package com.lms.adminpages.classrooms.controller;

import com.lms.adminpages.classrooms.entity.Classroom;
import com.lms.adminpages.classrooms.entity.CourseFilter;
import com.lms.adminpages.classrooms.service.ClassroomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping("/classrooms-registration")
    public String showRegisterForm(Model model) {
        model.addAttribute("classroom", new Classroom());
        List<String> categories = classroomService.findAllCategories();
        model.addAttribute("categories", categories);

        return "adminpages/classroom-registration/index";
    }

    @PostMapping("/classrooms-registration")
    public String registerClassroom(
            @ModelAttribute Classroom classroom,
            RedirectAttributes ra) {

        // 강사 ID 숫자 체크
        try {
            Integer instructorId = classroom.getInstructorId();
            if (instructorId == null) {
                ra.addFlashAttribute("errorMessage", "강사 ID를 입력해주세요.");
                return "redirect:/admin/classrooms-registration";
            }
        } catch (NumberFormatException e) {
            ra.addFlashAttribute("errorMessage", "강사 ID는 숫자만 입력 가능합니다.");
            return "redirect:/admin/classrooms-registration";
        }

        classroomService.save(classroom);
        ra.addFlashAttribute("message", "강의실이 등록되었습니다.");
        return "redirect:/admin/classrooms-registration";
    }


    @GetMapping("/classrooms-list")
    public String listClassrooms(
            @ModelAttribute("filter") CourseFilter filter,
            Model model
    ) {
        List<String> categories = classroomService.findAllCategories();
        model.addAttribute("categories", categories);

        List<Classroom> courses;

        if ((filter.getCategory() == null || filter.getCategory().isEmpty()) &&
                (filter.getStatus() == null || filter.getStatus().isEmpty()) &&
                (filter.getKeyword() == null || filter.getKeyword().isEmpty())) {
            courses = classroomService.findAll();
        } else {
            courses = classroomService.findByFilterFromDB(filter);
        }

        model.addAttribute("courses", courses);

        return "adminpages/classroom-list/index";
    }

    // 상태 업데이트
    @PostMapping("/classrooms/update-status")
    public String updateClassroomStatus(@RequestParam Integer classroomId,
                                        @RequestParam String status,
                                        RedirectAttributes ra) {
        try {
            classroomService.updateStatus(classroomId, status);
            ra.addFlashAttribute("message", "상태가 업데이트되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "상태 업데이트 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/classrooms-list";
    }

    @PostMapping("/classrooms/delete-selected")
    public String deleteSelected(@RequestParam("ids") List<Integer> ids, RedirectAttributes ra) {
        try {
            classroomService.softDeleteByIds(ids);
            ra.addFlashAttribute("message", "선택한 강의실이 삭제 처리되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "삭제 처리 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/classrooms-list";
    }
}


