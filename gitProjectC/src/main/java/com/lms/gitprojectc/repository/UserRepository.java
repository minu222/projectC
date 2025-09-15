package com.lms.gitprojectc.repository;

import com.lms.gitprojectc.domain.Role;
import com.lms.gitprojectc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByRole(Role role); // role이 "instructor"인 유저 조회
}