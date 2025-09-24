package com.lms.adminpages.classrooms.controller;

import com.lms.adminpages.classrooms.entity.MockExam;
import com.lms.adminpages.classrooms.service.MockExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class ExamQuestionManagement {



    @Autowired
    private MockExamService mockExamService;

    // 목록
    @GetMapping("/mock-exams")
    public String listExams(@RequestParam(required = false) Integer instructorId, Model model) {
        List<MockExam> exams;
        if (instructorId != null) {
            exams = mockExamService.searchExamsByInstructorId(instructorId);
        } else {
            exams = mockExamService.getAllExams();
        }
        model.addAttribute("exams", exams);
        return "adminpages/exam-questions-management/index";
    }


    // 등록 폼
    @GetMapping("/mock-exams/new")
    public String showCreateForm(Model model) {
        model.addAttribute("mockExam", new MockExam());
        return "adminpages/exam-questions-management/form";
    }

    // 등록 처리
    @PostMapping("/mock-exams/save")
    public String saveOrUpdateExam(@ModelAttribute MockExam exam, RedirectAttributes ra) {
        if (exam.getExamId() == null) {
            // 등록
            mockExamService.saveExam(exam);
            ra.addFlashAttribute("message", "시험 문제가 등록되었습니다.");
        } else {
            // 수정
            mockExamService.updateExam(exam);
            ra.addFlashAttribute("message", "시험 문제가 수정되었습니다.");
        }
        return "redirect:/admin/mock-exams";
    }
    // 수정 폼
    @GetMapping("/mock-exams/{id}/edit")
    public String editExamForm(@PathVariable int id, Model model) {
        MockExam mockExam = mockExamService.getExamById(id);
        model.addAttribute("mockExam", mockExam);
        return "adminpages/exam-questions-management/form"; // 수정폼 HTML
    }


    @PostMapping("/mock-exams/{id}/edit")
    public String updateExam(@PathVariable int id, @ModelAttribute MockExam mockExam, RedirectAttributes ra) {
        mockExam.setExamId(id); // 경로에서 받은 id를 객체에 세팅
        mockExamService.updateExam(mockExam);
        return "redirect:/admin/mock-exams"; // 수정 후 목록으로
    }

    // 삭제
    @PostMapping("/mock-exams/{id}/delete")
    public String deleteExam(@PathVariable("id") int id, RedirectAttributes ra) {
        mockExamService.deleteExam(id);
        ra.addFlashAttribute("message", "시험 문제가 삭제되었습니다.");
        return "redirect:/admin/mock-exams";
    }
}

