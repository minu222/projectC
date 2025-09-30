package com.lms.adminpages.classrooms.controller;

import com.lms.adminpages.classrooms.entity.Classroom;
import com.lms.adminpages.classrooms.entity.CourseFilter;
import com.lms.adminpages.classrooms.entity.StudentDto;
import com.lms.adminpages.classrooms.service.ClassroomService;
import com.lms.adminpages.users.dao.UserDao;
import com.lms.adminpages.users.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.management.relation.Role;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class ClassroomController {

    private final ClassroomService classroomService;
    private final UserDao userDao;
    private final JdbcTemplate jdbcTemplate;

    public ClassroomController(ClassroomService classroomService, UserDao userDao, JdbcTemplate jdbcTemplate) {
        this.classroomService = classroomService;
        this.userDao = userDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    //-----------------------------------강의실 등록
    @GetMapping("/classrooms-registration")
    public String showRegisterForm(Model model) {
        model.addAttribute("classroom", new Classroom());
        List<User> instructors = classroomService.findAllInstructors();
        model.addAttribute("instructors", instructors);
        List<String> categories = classroomService.findAllCategories();
        model.addAttribute("categories", categories);


        return "adminpages/classroom-registration/index";
    }

    @PostMapping("/classrooms-registration")
    public String registerClassroom(
            @ModelAttribute Classroom classroom,
            RedirectAttributes ra) {

        // 강사 ID 숫자 체크

        Integer instructorId = classroom.getInstructorId();
        if (instructorId == null) {
            ra.addFlashAttribute("errorMessage", "강사 ID를 입력해주세요.");
            return "redirect:/admin/classrooms-registration";
        }

        // 강사 존재 여부 및 상태 확인
        User instructor = classroomService.findUserById(instructorId);
        if (instructor == null) {
            ra.addFlashAttribute("errorMessage", "존재하지 않는 강사 ID입니다.");
            return "redirect:/admin/classrooms-registration";
        }

        if ("deleted".equalsIgnoreCase(instructor.getStatus().name())) {
            // status가 enum 이라면 .name()으로 비교
            ra.addFlashAttribute("errorMessage", "해당 강사는 탈퇴된 상태입니다.");
            return "redirect:/admin/classrooms-registration";
        }

        classroomService.save(classroom);
        ra.addFlashAttribute("message", "강의실이 등록되었습니다.");
        return "redirect:/admin/classrooms-registration";
    }
//    --------------


    //---------------------------강의실 목록-----------------
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
    @PostMapping("/classrooms/update-status-bulk")
    public String updateStatusBulk(
            @RequestParam Map<String, String> statusMap,
            @RequestParam(value = "saveSingle", required = false) Integer singleId,
            RedirectAttributes ra
    ) {
        Map<Integer, String> statusMapInt;

        if (singleId != null) {
            // 한 행만 저장
            String status = statusMap.get("statusMap[" + singleId + "]");
            statusMapInt = Map.of(singleId, status);
        } else {
            // 전체 저장
            statusMapInt = statusMap.entrySet().stream()
                    .filter(e -> e.getKey().startsWith("statusMap["))
                    .collect(Collectors.toMap(
                            e -> Integer.parseInt(e.getKey().replaceAll("statusMap\\[|\\]", "")),
                            Map.Entry::getValue
                    ));
        }

        classroomService.updateStatus(statusMapInt);

        ra.addFlashAttribute("successMessage", "강의실 상태가 업데이트되었습니다.");
        return "redirect:/admin/classrooms-list";
    }

    //선택 삭제
    @PostMapping("/classrooms/delete-selected")
    public String deleteSelected(
            @RequestParam(value = "ids", required = false) List<Integer> ids,
            RedirectAttributes ra
    ) {
        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "삭제할 강의실을 선택해주세요.");
            return "redirect:/admin/classrooms-list";
        }

        classroomService.deleteByIds(ids);
        ra.addFlashAttribute("successMessage", "선택한 강의실이 삭제되었습니다.");
        return "redirect:/admin/classrooms-list";
    }

//    -----------------------------------------



    @GetMapping("/attendance-management")
    public String attendanceClassroom() {
        return "adminpages/attendance-management/index";
    }

}
