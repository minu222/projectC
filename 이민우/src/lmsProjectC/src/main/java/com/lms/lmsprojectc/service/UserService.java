package com.lms.lmsprojectc.service;

import com.lms.lmsprojectc.domain.Role;
import com.lms.lmsprojectc.entity.User;
import com.lms.lmsprojectc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 전체 유저 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 강사만 조회
    public List<User> getAllInstructors() {
        return userRepository.findByRole(Role.instructor);
    }


    // 유저 저장
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 유저 삭제
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
