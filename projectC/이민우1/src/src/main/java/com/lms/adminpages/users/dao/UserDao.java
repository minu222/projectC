package com.lms.adminpages.users.dao;

import com.lms.adminpages.users.entity.User;
import com.lms.adminpages.users.entity.User.Role;
import com.lms.adminpages.users.entity.User.Status;
import com.lms.adminpages.users.entity.User.Gender;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findInstructors(String status, String department, String keywordType, String keyword) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE role = 'instructor'");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        if (department != null && !department.isEmpty()) {
            sql.append(" AND address = ?");
            params.add(department);
        }

        if (keyword != null && !keyword.isEmpty()) {
            if ("name".equals(keywordType)) {
                sql.append(" AND name LIKE ?");
                params.add("%" + keyword + "%");
            } else if ("nickname".equals(keywordType)) {
                sql.append(" AND nickname LIKE ?");
                params.add("%" + keyword + "%");
            } else {
                sql.append(" AND (name LIKE ? OR nickname LIKE ?)");
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");
            }
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), new UserRowMapper());
    }


    // =============================
    // 동적 필터 적용 가능한 학생 조회
    // =============================
    public List<User> findStudents(String status, String department, String keywordType, String keyword) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE role = 'student'");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        if (department != null && !department.isEmpty()) {
            sql.append(" AND address = ?");
            params.add(department);
        }

        if (keyword != null && !keyword.isEmpty()) {
            if ("name".equals(keywordType)) {
                sql.append(" AND name LIKE ?");
                params.add("%" + keyword + "%");
            } else if ("nickname".equals(keywordType)) {
                sql.append(" AND nickname LIKE ?");
                params.add("%" + keyword + "%");
            } else {
                sql.append(" AND (name LIKE ? OR nickname LIKE ?)");
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");
            }
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), new UserRowMapper());
    }


    // =============================
    // 공통 RowMapper
    public class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUser_id(rs.getInt("user_id"));
            user.setNickname(rs.getString("nickname"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setName(rs.getString("name"));
            user.setPhone(rs.getString("phone"));
            user.setAddress(rs.getString("address"));
            user.setBirth_day(rs.getDate("birth_day") != null ? rs.getDate("birth_day").toLocalDate() : null);
            user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender").toLowerCase()) : null);
            user.setEmail_verified(rs.getBoolean("email_verified"));

            // Role
            String roleStr = rs.getString("role");
            if (roleStr != null) {
                user.setRole(User.Role.valueOf(roleStr.toLowerCase()));
            }

            // Status
            String statusStr = rs.getString("status");
            if (statusStr != null) {
                user.setStatus(User.Status.valueOf(statusStr.toLowerCase()));
            }

            user.setCreated_at(rs.getTimestamp("created_at") != null ?
                    rs.getTimestamp("created_at").toLocalDateTime() : null);

            user.setUpdated_at(rs.getTimestamp("updated_at") != null ?
                    rs.getTimestamp("updated_at").toLocalDateTime() : null);

            return user;
        }
    }


    public void updateStatusToDeleted(int[] ids) {
        String sql = "UPDATE users SET status = 'deleted' WHERE user_id = ?";
        for (int id : ids) {
            jdbcTemplate.update(sql, id);
        }
    }


}
