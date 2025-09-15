package com.example.projectc.web;


import com.example.projectc.entity.Order;
import com.example.projectc.entity.User;
import com.example.projectc.repo.UserRepository;
import com.example.projectc.service.OrderService;
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
