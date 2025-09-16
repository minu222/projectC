package com.lms.lmsprojectc.entity;


import com.lms.lmsprojectc.domain.Gender;
import com.lms.lmsprojectc.domain.Role;
import com.lms.lmsprojectc.domain.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;


@Entity @Table(name="users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;


    @Column(nullable = false, unique = true)
    private String email;


    @Column(nullable = false)
    private String password;


    @Column(nullable = false)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    private String address;


    @Enumerated(EnumType.STRING)
    private Role role = Role.student;


    private LocalDate birth_date;


    @Enumerated(EnumType.STRING)
    private Gender gender;


    private boolean email_verified;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.active;


    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
