package com.lms.adminpages.users.entity;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private int user_id;
    private String nickname;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;
    private Role role;
    private LocalDate birth_day;
    private Gender gender;
    private boolean email_verified;
    private Status status;
    private Timestamp created_at;
    private Timestamp updated_at;

    public enum Role {
        student, instructor, admin
    }

    public enum Gender {
        male, female
    }

    public enum Status {
        active, suspended, deleted
    }
}
