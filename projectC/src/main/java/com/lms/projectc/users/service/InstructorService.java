package com.lms.projectc.users.service;

import com.lms.projectc.users.entity.User;
import com.lms.projectc.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructorService {

    private final UserRepository userRepository;

    public InstructorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findInstructors();
    }

    public User findById(int id) {
        return userRepository.findById(id);
    }

    public List<User> searchInstructors(String department, String keywordType, String keyword, String status) {
        return userRepository.findInstructorsWithFilter(department, keywordType, keyword, status);
    }

}
