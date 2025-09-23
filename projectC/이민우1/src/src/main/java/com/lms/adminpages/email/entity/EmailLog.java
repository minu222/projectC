package com.lms.adminpages.email.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmailLog {
    private Long id;
    private String recipients;
    private String subject;
    private String content;
    private String attachments;
    private String status;
    private LocalDateTime createdAt;

    // Getter/Setter
}