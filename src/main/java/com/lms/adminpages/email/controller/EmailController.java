package com.lms.adminpages.email.controller;


import com.lms.adminpages.email.entity.EmailForm;
import com.lms.adminpages.email.entity.EmailLog;
import com.lms.adminpages.email.service.EmailLogService;
import com.lms.adminpages.email.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;
    private final EmailLogService emailLogService;

    public EmailController(EmailService emailService, EmailLogService emailLogService) {
        this.emailService = emailService;
        this.emailLogService = emailLogService;
    }


    @GetMapping("/compose-instructor-email")
    public String showComposeEmailForm(Model model) {
        model.addAttribute("emailForm", new EmailForm());
        return "adminpages/compose-instructor-email/index";
    }


    @PostMapping("/compose-instructor-email")
    public String sendEmail(@ModelAttribute("emailForm") EmailForm form,
                            RedirectAttributes ra) {
        try {
            // ✅ 메일 발송
            emailService.sendEmail(form);

            // ✅ 성공 로그 저장
            emailLogService.saveLog(
                    String.join(",", form.getRecipients()),
                    form.getSubject(),
                    form.getContent(),
                    form.getAttachments() != null ? form.getAttachments().toString() : null,
                    "sent"
            );

            ra.addFlashAttribute("successMessage", "메일이 성공적으로 발송되었습니다!");
        } catch (Exception e) {
            // ❌ 실패 로그 저장
            emailLogService.saveLog(
                    String.join(",", form.getRecipients()),
                    form.getSubject(),
                    form.getContent(),
                    null,
                    "failed"
            );

            ra.addFlashAttribute("errorMessage", "메일 발송 실패: " + e.getMessage());
        }
        return "redirect:/email/compose-instructor-email";
    }


    @GetMapping("/email-send-history")
    public String listEmailLogs(Model model) {
        List<EmailLog> logs = emailLogService.getAllLogs(); // 전체 조회
        model.addAttribute("logs", logs);
        return "adminpages/email-send-history/index";
    }
}

