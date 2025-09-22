package com.lms.adminpages.email.service;

import com.lms.adminpages.email.entity.EmailForm;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(EmailForm form) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // 수신자 (리스트 → 문자열 배열로 변환)
        if (form.getRecipients() != null && !form.getRecipients().isEmpty()) {
            helper.setTo(form.getRecipients().toArray(new String[0]));
        }

        helper.setSubject(form.getSubject());
        helper.setText(form.getContent(), true); // HTML 형식 허용
        helper.setFrom("no-reply@example.com");

        // 첨부파일 처리
        if (form.getAttachments() != null) {
            for (MultipartFile file : form.getAttachments()) {
                if (!file.isEmpty()) {
                    helper.addAttachment(file.getOriginalFilename(), file);
                }
            }
        }

        mailSender.send(message);
    }


}
