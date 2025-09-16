package com.lms.lmsprojectc.repo;

import com.lms.lmsprojectc.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart,Long> {
    List<Cart> findByUserId(Long userId);
}
