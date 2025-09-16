package com.lms.projectc.users.repository;
import com.lms.projectc.users.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    //회원정보 저장
    public User save(User user) {
        System.out.println("==> user added");
        String sql = "INSERT INTO users (nickname ,email, password ,name,phone,addsress,role,birth_day,gender,status ,created_at,updated_at)"+
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?,now(),now())";
        int result = jdbc.update(sql,
                user.getNickname(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getAddress(),
                user.getRole(),
                java.sql.Date.valueOf(user.getBirth_day()),
                user.getStatus(),
                user.getCreated_at(),
                user.getUpdated_at()
        );
        if (result == 1) {
            System.out.println(result);
            System.out.println("회원 등록 성공");
            return user;
        }else {
            System.out.println("회원등록 실패");
            return null;
        }
    }


    public void add(User user) {

        System.out.println("==> user added");
        String sql = "INSERT INTO users (nickname ,email, password ,name,phone,addsress,role,birth_day,gender,status ,created_at,updated_at)"+
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?,now(),now())";
        int result = jdbc.update(sql,
                user.getNickname(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getAddress(),
                user.getRole(),
                java.sql.Date.valueOf(user.getBirth_day()),
                user.getStatus(),
                user.getCreated_at(),
                user.getUpdated_at()
        );
        if (result == 1) {
            System.out.println(result);
            System.out.println("회원 등록 성공");
        }else {
            System.out.println("회원등록 실패");
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, userRowMapper());
    }



    public Optional<User> findByUsername(String username) {
        System.out.println("==>" + username);
        String sql = "SELECT * FROM users WHERE username = ?";

        try{
            User user = jdbc.queryForObject(sql, userRowMapper(),username);
            return Optional.of(user);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }



    }

    public Optional<User> findById(String custid) {
        System.out.println("==>" + custid);
        String sql = "SELECT * FROM users WHERE custid = ?";
        User user = jdbc.queryForObject(sql, userRowMapper(),custid);
        return Optional.of(user);

    }



    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            Date birthDate = rs.getDate("birth_day");
            LocalDate birth_day = birthDate != null ? ((java.sql.Date) birthDate).toLocalDate() : null;

            String roleStr = rs.getString("role");
            User.Role role = roleStr != null ? User.Role.valueOf(roleStr.toUpperCase()) : null;

            String genderStr = rs.getString("gender");
            User.Gender gender = genderStr != null ? User.Gender.valueOf(genderStr.toUpperCase()) : null;

            String statusStr = rs.getString("status");
            User.Status status = statusStr != null ? User.Status.valueOf(statusStr.toUpperCase()) : null;


            return new User(
                    rs.getInt("user_id"),
                    rs.getString("nickname"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    role,
                    birth_day,
                    gender,
                    status,
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            );
        };
    }

    // 사용자 수정
    public void update(User user) {
        String sql = "UPDATE users SET address = ?, email = ?,phone = ?, WHERE user_id = ?";
        jdbc.update(sql,
                user.getAddress(),
                user.getEmail(),
                user.getPhone(),
                user.getUser_id());
    }

    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        int rowsAffected = jdbc.update(sql, id);
        if(rowsAffected > 0){
            System.out.println(rowsAffected);
        }else {
            System.out.println("삭제 할 사용자가 없습니다."+id);
        }
    }

}
