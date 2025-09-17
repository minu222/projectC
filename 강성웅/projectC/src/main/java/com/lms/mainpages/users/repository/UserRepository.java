package com.lms.mainpages.users.repository;

import com.lms.mainpages.users.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
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

    /**
     * 사용자 저장 + 생성된 PK(user_id) 반환
     * - Enum은 name()으로 저장
     * - LocalDate는 java.sql.Date로 변환
     * - created_at/updated_at는 DB NOW() 사용
     */
    public long saveAndReturnId(User user) {
        final String sql = """
            INSERT INTO users
              (nickname, email, password, name, phone, address, role, birth_day, gender, status, created_at, updated_at)
            VALUES
              (?,        ?,     ?,        ?,    ?,     ?,      ?,    ?,         ?,      ?,      NOW(),     NOW())
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setString(i++, user.getNickname());
            ps.setString(i++, user.getEmail());
            ps.setString(i++, user.getPassword());
            ps.setString(i++, user.getName());
            ps.setString(i++, user.getPhone());
            ps.setString(i++, user.getAddress());
            ps.setString(i++, user.getRole()   != null ? user.getRole().name()   : null);

            LocalDate birth = user.getBirth_day();
            if (birth != null) ps.setDate(i++, Date.valueOf(birth)); else ps.setNull(i++, Types.DATE);

            ps.setString(i++, user.getGender() != null ? user.getGender().name() : null);
            ps.setString(i++, user.getStatus() != null ? user.getStatus().name() : null);

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) throw new IllegalStateException("user_id 생성 실패");
        user.setUser_id(key.intValue());
        return key.longValue();
    }

    /**
     * 사용자 저장 (User 객체에 user_id 세팅 후 반환)
     */
    public User save(User user) {
        saveAndReturnId(user);
        return user;
    }

    /** 기존 add 메서드는 save 위임 */
    public void add(User user) {
        save(user);
    }

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
            Date birthSql = rs.getDate("birth_day");
            LocalDate birth = (birthSql != null) ? birthSql.toLocalDate() : null;

            User.Role role     = toRole(rs.getString("role"));
            User.Gender gender = toGender(rs.getString("gender"));
            User.Status status = toStatus(rs.getString("status"));

            return new User(
                    rs.getInt("user_id"),
                    rs.getString("nickname"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("address"),   // ⚠️ DB 컬럼명이 address 인지 확인
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
        try { return User.Role.valueOf(s.trim().toUpperCase()); }
        catch (Exception e) { return null; }
    }

    private User.Gender toGender(String s) {
        if (s == null) return null;
        try { return User.Gender.valueOf(s.trim().toUpperCase()); }
        catch (Exception e) { return null; }
    }

    private User.Status toStatus(String s) {
        if (s == null) return null;
        try { return User.Status.valueOf(s.trim().toUpperCase()); }
        catch (Exception e) { return null; }
    }

    /**
     * 일부 정보 수정
     */
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
            System.out.println("삭제할 사용자가 없습니다. id=" + userId);
        }
    }

    public int updateProfileFields(int userId, String email, String phone, String address) {
        return userId;
    }
}