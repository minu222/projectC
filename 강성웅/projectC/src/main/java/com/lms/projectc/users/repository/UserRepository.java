package com.lms.projectc.users.repository;

import com.lms.projectc.users.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** 회원 저장 (null-safe, 컬럼/파라미터 정렬) */
    public User save(User user) {
        // ※ 테이블 컬럼명이 address 인지 확인하세요. (addsress 였다면 DB를 고치거나 아래를 맞추세요)
        final String sql =
                "INSERT INTO users (" +
                        "  nickname, email, password, name, phone, address, role, birth_day, gender, status, created_at, updated_at" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())";

        int updated = jdbc.update(sql, ps -> {
            int i = 1;
            ps.setString(i++, user.getNickname());
            ps.setString(i++, user.getEmail());
            ps.setString(i++, user.getPassword());
            ps.setString(i++, user.getName());
            ps.setString(i++, user.getPhone());
            ps.setString(i++, user.getAddress());

            // role (ENUM → VARCHAR), null-safe
            if (user.getRole() != null) ps.setString(i++, user.getRole().name());
            else ps.setNull(i++, Types.VARCHAR);

            // birth_day (LocalDate → DATE), null-safe
            LocalDate birth = user.getBirth_day();
            if (birth != null) ps.setDate(i++, Date.valueOf(birth));
            else ps.setNull(i++, Types.DATE);

            // gender (ENUM → VARCHAR), null-safe
            if (user.getGender() != null) ps.setString(i++, user.getGender().name());
            else ps.setNull(i++, Types.VARCHAR);

            // status (ENUM → VARCHAR), null-safe
            if (user.getStatus() != null) ps.setString(i++, user.getStatus().name());
            else ps.setNull(i++, Types.VARCHAR);
        });

        return (updated == 1) ? user : null;
    }

    /** save와 동일 동작 */
    public void add(User user) { save(user); }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, userRowMapper());
    }

    public Optional<User> findByUsername(String nickname) {
        String sql = "SELECT * FROM users WHERE nickname = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, userRowMapper(), nickname));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, userRowMapper(), userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            Date d = rs.getDate("birth_day");
            LocalDate birth = (d != null) ? d.toLocalDate() : null;

            User.Role role = toRole(rs.getString("role"));
            User.Gender gender = toGender(rs.getString("gender"));
            User.Status status = toStatus(rs.getString("status"));

            return new User(
                    rs.getInt("user_id"),
                    rs.getString("nickname"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    role,
                    birth,
                    gender,
                    status,
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            );
        };
    }

    private User.Role toRole(String s) {
        if (s == null) return null;
        try { return User.Role.valueOf(s.toUpperCase()); } catch (Exception e) { return null; }
    }
    private User.Gender toGender(String s) {
        if (s == null) return null;
        try { return User.Gender.valueOf(s.toUpperCase()); } catch (Exception e) { return null; }
    }
    private User.Status toStatus(String s) {
        if (s == null) return null;
        try { return User.Status.valueOf(s.toUpperCase()); } catch (Exception e) { return null; }
    }

    /** 일부 정보 수정 (콤마 오류 제거) */
    public void update(User user) {
        String sql = "UPDATE users SET address = ?, email = ?, phone = ? WHERE user_id = ?";
        jdbc.update(sql,
                user.getAddress(),
                user.getEmail(),
                user.getPhone(),
                user.getUser_id()
        );
    }

    public void deleteById(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        int rows = jdbc.update(sql, userId);
        if (rows == 0) {
            System.out.println("삭제 할 사용자가 없습니다. id=" + userId);
        }
    }
}
