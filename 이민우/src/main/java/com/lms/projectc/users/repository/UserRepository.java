package com.lms.projectc.users.repository;

import com.lms.projectc.users.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 강사 목록 조회
    public List<User> findInstructors() {
        String sql = "SELECT * FROM users WHERE role = 'instructor'";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    // 강사 단일 조회
    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, new UserRowMapper());
    }

    // RowMapper 정의
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUser_id(rs.getInt("user_id"));
            user.setNickname(rs.getString("nickname"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setAddress(rs.getString("address"));
            user.setPassword(rs.getString("password"));
            user.setRole(User.Role.valueOf(rs.getString("role")));
            user.setGender(User.Gender.valueOf(rs.getString("gender")));
            user.setBirth_day(rs.getDate("birth_day").toLocalDate());
            user.setStatus(User.Status.valueOf(rs.getString("status")));
            user.setCreated_at(rs.getTimestamp("created_at"));
            user.setUpdated_at(rs.getTimestamp("updated_at"));
            return user;
        }
    }

    public List<User> findStudents() {
        String sql = "SELECT * FROM users WHERE role = 'student'";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public List<User> findInstructorsWithFilter(String department, String keywordType, String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE role = 'instructor'");

        List<Object> params = new ArrayList<>();

        if (department != null && !department.isEmpty()) {
            sql.append(" AND department = ?");
            params.add(department);
        }

        if (keyword != null && !keyword.isEmpty() && keywordType != null && !keywordType.isEmpty()) {
            sql.append(" AND ").append(keywordType).append(" LIKE ?");
            params.add("%" + keyword + "%");
        }

        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), new UserRowMapper());
    }



}
