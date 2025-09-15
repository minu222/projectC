package com.example.projectc.repo;
import com.example.projectc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);   // ★ 추가
    boolean existsByNickname(String nickname);
}
