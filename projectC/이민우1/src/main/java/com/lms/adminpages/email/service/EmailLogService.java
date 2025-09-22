package com.lms.adminpages.email.service;

import com.lms.adminpages.email.dao.EmailLogDao;
import com.lms.adminpages.email.entity.EmailLog;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailLogService {

    private final EmailLogDao emailLogDao;

    public EmailLogService(EmailLogDao emailLogDao) {
        this.emailLogDao = emailLogDao;
    }

    public void saveLog(String recipients, String subject, String content, String attachments, String status) {
        emailLogDao.saveLog(recipients, subject, content, attachments, status);
    }

    public List<EmailLog> getAllLogs() {
        return emailLogDao.findAllLogs();
    }
}