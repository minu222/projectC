package com.example.projectc.service;


import com.example.projectc.entity.*;
import com.example.projectc.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;


@Service @RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;


    public List<Cart> findByUser(Long userId) {
        return cartRepository.findByUserId(userId);
    }


    @Transactional
    public Cart add(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        Cart cart = Cart.builder().user(user).course(course).build();
        return cartRepository.save(cart);
    }


    @Transactional
    public void remove(Long cartId) { cartRepository.deleteById(cartId); }
}
