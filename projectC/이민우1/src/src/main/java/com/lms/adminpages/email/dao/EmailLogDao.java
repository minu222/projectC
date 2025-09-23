package com.lms.adminpages.email.dao;

import com.lms.adminpages.email.entity.EmailLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmailLogDao {

    private final JdbcTemplate jdbcTemplate;

    public EmailLogDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 저장
    public int saveLog(String recipients, String subject, String content, String attachments, String status) {
        String sql = "INSERT INTO email_logs (recipients, subject, content, attachments, status) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, recipients, subject, content, attachments, status);
    }

    // 전체 조회
    public List<EmailLog> findAllLogs() {
        String sql = "SELECT * FROM email_logs ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new EmailLogRowMapper());
    }

    // RowMapper
    private static class EmailLogRowMapper implements RowMapper<EmailLog> {
        @Override
        public EmailLog mapRow( ResultSet rs, int rowNum) throws SQLException {
            EmailLog log = new EmailLog();
            log.setId(rs.getLong("id"));
            log.setRecipients(rs.getString("recipients"));
            log.setSubject(rs.getString("subject"));
            log.setContent(rs.getString("content"));
            log.setAttachments(rs.getString("attachments"));
            log.setStatus(rs.getString("status"));
            log.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return log;
        }
    }
}