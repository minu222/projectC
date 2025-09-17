package com.lms.projectc.users.service;


import com.lms.projectc.users.entity.User;
import com.lms.projectc.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public int join(User user){
        System.out.println("==> user joined");
        validateDuplicateUser(user);
        userRepo.save(user);
        return user.getUser_id();
    }

    private void validateDuplicateUser(User user) {
        userRepo.findByUsername(user.getNickname())
                .ifPresent(m -> {
                    throw new IllegalArgumentException("이미 존재하는 아이디 입니다.");
                });
    }

    public void register(User user){
        userRepo.add(user);
    }


    public List<User> findAll() {
        return userRepo.findAll();
    }


    //로그인
    public Optional<User> login(String username, String password) {
        return userRepo.findByUsername(username)
                .filter(user -> user.getPassword().equals(password));
    }

    public Optional<User> findById(String id) {
        return userRepo.findById(Integer.parseInt(id));
    }

    public void update(User user) {
        userRepo.update(user);
    }

    public void deleteById(String id) {
        userRepo.deleteById(Integer.parseInt(id));
    }
}
