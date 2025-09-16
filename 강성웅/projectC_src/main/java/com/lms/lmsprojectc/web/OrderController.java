package com.lms.lmsprojectc.web;


import com.lms.lmsprojectc.entity.Order;
import com.lms.lmsprojectc.entity.User;
import com.lms.lmsprojectc.repo.UserRepository;
import com.lms.lmsprojectc.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController @RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepository;


    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Order order = orderService.checkout(user);
        return ResponseEntity.ok(order);
    }
}
