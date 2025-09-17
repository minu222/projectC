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

        if (keyword != null && !keyword.isEmpty() && keywordType != null && !keywordType.isEmpty()) {
            if (keywordType.equals("name")) {
                sql.append(" AND name LIKE ?");
            } else if (keywordType.equals("nickname")) {
                sql.append(" AND nickname LIKE ?");
            }
            params.add("%" + keyword + "%");
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

        if (keyword != null && !keyword.isEmpty() && keywordType != null && !keywordType.isEmpty()) {
            if (keywordType.equals("name")) {
                sql.append(" AND name LIKE ?");
            } else if (keywordType.equals("nickname")) {
                sql.append(" AND nickname LIKE ?");
            }
            params.add("%" + keyword + "%");
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
            user.setName(rs.getString("name"));
            user.setPhone(rs.getString("phone"));
            user.setAddress(rs.getString("address"));

            // Role
            String roleStr = rs.getString("role");
            user.setRole(roleStr != null ? User.Role.valueOf(roleStr) : User.Role.instructor);

            // Status
            String statusStr = rs.getString("status");
            user.setStatus(statusStr != null ? User.Status.valueOf(statusStr) : User.Status.active);

            // Timestamp
            user.setCreated_at(rs.getTimestamp("created_at"));
            user.setUpdated_at(rs.getTimestamp("updated_at"));

            return user;
        }
    }
}
