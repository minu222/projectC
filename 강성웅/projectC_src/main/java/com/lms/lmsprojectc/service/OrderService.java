package com.lms.lmsprojectc.service;


import com.lms.lmsprojectc.domain.OrderStatus;
import com.lms.lmsprojectc.entity.*;
import com.lms.lmsprojectc.repo.*;
import com.lms.lmsprojectc.entity.Cart;
import com.lms.lmsprojectc.entity.Enrollment;
import com.lms.lmsprojectc.entity.Order;
import com.lms.lmsprojectc.entity.User;
import com.lms.lmsprojectc.repo.CartRepository;
import com.lms.lmsprojectc.repo.EnrollmentRepository;
import com.lms.lmsprojectc.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;


@Service @RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final EnrollmentRepository enrollmentRepository;


    @Transactional
    public Order checkout(User user) {
        List<Cart> items = cartRepository.findByUserId(user.getId());
        if (items.isEmpty()) throw new IllegalStateException("장바구니가 비어있습니다.");


        BigDecimal total = items.stream()
                .map(c -> c.getCourse().getPrice())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        Order order = Order.builder()
                .user(user)
                .total_amount(total)
                .status(OrderStatus.paid) // 결제 성공 가정
                .payment_method("card")
                .created_at(LocalDateTime.now())
                .build();
        orderRepository.save(order);


// 수강 등록
        for (Cart c : items) {
            Enrollment en = Enrollment.builder()
                    .order(order)
                    .student(user)
                    .course(c.getCourse())
                    .enrolled_at(LocalDateTime.now())
                    .build();
            enrollmentRepository.save(en);
        }


// 장바구니 비우기
        cartRepository.deleteAll(items);
        return order;
    }
}
