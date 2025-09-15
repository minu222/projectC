package com.lms.gitprojectc.service;

import com.lms.gitprojectc.domain.Role;
import com.lms.gitprojectc.entity.User;
import com.lms.gitprojectc.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllInstructors() {
        return userRepository.findByRole(Role.instructor); // DB에서 강사만 조회
    }

    public User getInstructorById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Instructor not found"));
    }


    public List<User> getAllStudents() {
        return userRepository.findByRole(Role.student);
    }
}