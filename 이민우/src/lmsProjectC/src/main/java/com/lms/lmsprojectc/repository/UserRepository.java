package com.lms.lmsprojectc.repository;

import com.lms.lmsprojectc.domain.Role;
import com.lms.lmsprojectc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    // 특정 Role(예: instructor) 만 가져오기
    List<User> findByRole(Role role);


}