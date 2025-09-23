package com.lms.adminpages.email.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class EmailForm {
    private String subject;
    private String template;
    private String content;
    private List<String> recipients; // comma-separated 이메일
    private MultipartFile[] attachments;
    // getters & setters
}