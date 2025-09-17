package com.lms.projectc.users.service;

import com.lms.projectc.users.entity.User;
import com.lms.projectc.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final UserRepository userRepository;

    public StudentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findStudents();
    }

    public User findById(int id) {
        return userRepository.findById(id);
    }
}
